package com.example.demo;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.NazoShop.NazoItem;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String saveOrder(
            List<NazoItem> cart,
            String customerName,
            String email,
            String address) {

        if (cart.isEmpty()) {
            throw new IllegalStateException(
                    "カートが空です。");
        }

        if (customerName.isBlank()
                || email.isBlank()
                || address.isBlank()) {

            throw new IllegalStateException(
                    "顧客情報が入力されていません。");
        }

        String stockSql = """
                UPDATE items
                SET stock = stock - ?
                WHERE id = ?
                  AND active = TRUE
                  AND stock >= ?
                """;

        String latestItemSql = """
                SELECT
                    name AS "name",
                    price AS "price"
                FROM items
                WHERE id = ?
                  AND active = TRUE
                """;

        List<NazoItem> latestCart = new ArrayList<>();

        for (NazoItem item : cart) {

            int updatedRows = jdbcTemplate.update(
                    stockSql,
                    item.quantity(),
                    item.id(),
                    item.quantity());

            if (updatedRows == 0) {
                throw new IllegalStateException(
                        "在庫が不足しているか、販売が停止されています。");
            }

            Map<String, Object> latestItem = jdbcTemplate.queryForMap(
                    latestItemSql,
                    item.id());

            String latestName = latestItem.get("name").toString();

            int latestPrice = ((Number) latestItem.get("price"))
                    .intValue();

            latestCart.add(
                    new NazoItem(
                            item.id(),
                            latestName,
                            latestPrice,
                            item.quantity()));
        }

        String customerSql = """
                INSERT INTO customers
                    (
                        name,
                        email,
                        address,
                        created_at
                    )
                VALUES
                    (?, ?, ?, CURRENT_TIMESTAMP)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int insertedRows = jdbcTemplate.update(
                connection -> {

                    /*
                     * 自動生成される列のうち、
                     * customersテーブルのidだけを取得する。
                     */
                    PreparedStatement statement = connection.prepareStatement(
                            customerSql,
                            new String[] { "id" });

                    statement.setString(
                            1,
                            customerName);

                    statement.setString(
                            2,
                            email);

                    statement.setString(
                            3,
                            address);

                    return statement;
                },
                keyHolder);

        if (insertedRows != 1) {
            throw new IllegalStateException(
                    "顧客情報を保存できませんでした。");
        }

        Number generatedCustomerId = keyHolder.getKey();

        if (generatedCustomerId == null) {
            throw new IllegalStateException(
                    "登録した顧客IDを取得できませんでした。");
        }

        int customerId = generatedCustomerId.intValue();

        String orderId = UUID.randomUUID().toString();

        int totalPrice = 0;

        for (NazoItem item : latestCart) {

            totalPrice += item.price() * item.quantity();
        }

        String orderSql = """
                INSERT INTO orders
                    (
                        id,
                        customer_id,
                        order_time,
                        total_price
                    )
                VALUES
                    (?, ?, CURRENT_TIMESTAMP, ?)
                """;

        jdbcTemplate.update(
                orderSql,
                orderId,
                customerId,
                totalPrice);

        String orderItemSql = """
                INSERT INTO order_items
                    (
                        order_id,
                        item_id,
                        item_name,
                        price,
                        quantity
                    )
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        for (NazoItem item : latestCart) {

            jdbcTemplate.update(
                    orderItemSql,
                    orderId,
                    item.id(),
                    item.name(),
                    item.price(),
                    item.quantity());
        }

        return orderId;
    }

    public List<Map<String, Object>> getOrderHistory() {

        String sql = """
                SELECT
                    o.id AS "orderId",
                    o.order_time AS "orderTime",

                    c.name AS "customerName",
                    c.email AS "customerEmail",
                    c.address AS "customerAddress",

                    oi.item_name AS "itemName",
                    oi.price AS "price",
                    oi.quantity AS "quantity",
                    oi.price * oi.quantity AS "subtotal",

                    o.total_price AS "totalPrice"

                FROM orders o

                LEFT JOIN customers c
                  ON o.customer_id = c.id

                JOIN order_items oi
                  ON o.id = oi.order_id

                ORDER BY
                    o.order_time DESC,
                    oi.id
                """;

        return jdbcTemplate.queryForList(sql);
    }

    public int getTotalSales() {

        String sql = """
                SELECT COALESCE(SUM(total_price), 0)
                FROM orders
                """;

        Integer total = jdbcTemplate.queryForObject(
                sql,
                Integer.class);

        if (total == null) {
            return 0;
        }

        return total;
    }

    public int getOrderCount() {

        String sql = """
                SELECT COUNT(*)
                FROM orders
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class);

        if (count == null) {
            return 0;
        }

        return count;
    }
}