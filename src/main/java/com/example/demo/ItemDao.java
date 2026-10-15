package com.example.demo;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ItemDao {

    private final JdbcTemplate jdbcTemplate;

    public ItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // お客さんの画面に表示する商品
    public List<Map<String, Object>> getAllItems() {

        String sql = """
                SELECT
                    id AS "id",
                    name AS "name",
                    category AS "category",
                    company AS "company",
                    price AS "price",
                    stock AS "stock",
                    active AS "active"
                FROM items
                WHERE active = TRUE
                ORDER BY id
                """;

        return jdbcTemplate.queryForList(sql);
    }

    // 会社名で絞り込む
    public List<Map<String, Object>> getItemsByCompany(
            String company) {

        String sql = """
                SELECT
                    id AS "id",
                    name AS "name",
                    category AS "category",
                    company AS "company",
                    price AS "price",
                    stock AS "stock",
                    active AS "active"
                FROM items
                WHERE company = ?
                  AND active = TRUE
                ORDER BY id
                """;

        return jdbcTemplate.queryForList(
                sql,
                company);
    }

    // 商品名と会社名で検索する
    public List<Map<String, Object>> searchItems(
            String keyword,
            String company) {

        String safeKeyword = keyword == null ? "" : keyword.trim();

        String safeCompany = company == null ? "" : company.trim();

        String keywordPattern = "%" + safeKeyword + "%";

        String sql = """
                SELECT
                    id AS "id",
                    name AS "name",
                    category AS "category",
                    company AS "company",
                    price AS "price",
                    stock AS "stock",
                    active AS "active"
                FROM items
                WHERE active = TRUE
                  AND (
                        ? = ''
                        OR LOWER(name) LIKE LOWER(?)
                      )
                  AND (
                        ? = ''
                        OR company = ?
                      )
                ORDER BY id
                """;

        return jdbcTemplate.queryForList(
                sql,
                safeKeyword,
                keywordPattern,
                safeCompany,
                safeCompany);
    }

    // IDから販売中の商品を1件取得する
    public Map<String, Object> getItemById(
            int id) {

        String sql = """
                SELECT
                    id AS "id",
                    name AS "name",
                    category AS "category",
                    company AS "company",
                    price AS "price",
                    stock AS "stock",
                    active AS "active"
                FROM items
                WHERE id = ?
                  AND active = TRUE
                """;

        return jdbcTemplate.queryForMap(
                sql,
                id);
    }

    // 商品の現在庫を取得する
    public int getStockById(
            int id) {

        String sql = """
                SELECT stock
                FROM items
                WHERE id = ?
                """;

        Integer stock = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                id);

        if (stock == null) {
            return 0;
        }

        return stock;
    }

    // 管理者ページ用。販売停止商品も表示する
    public List<Map<String, Object>> getAllItemsForAdmin() {

        String sql = """
                SELECT
                    id AS "id",
                    name AS "name",
                    category AS "category",
                    company AS "company",
                    price AS "price",
                    stock AS "stock",
                    active AS "active"
                FROM items
                ORDER BY id
                """;

        return jdbcTemplate.queryForList(sql);
    }

    // 商品追加
    public void addItem(
            String name,
            String category,
            String company,
            int price,
            int stock) {

        String sql = """
                INSERT INTO items
                    (
                        name,
                        category,
                        company,
                        price,
                        stock,
                        active
                    )
                VALUES
                    (?, ?, ?, ?, ?, TRUE)
                """;

        jdbcTemplate.update(
                sql,
                name,
                category,
                company,
                price,
                stock);
    }

    // 価格変更
    public void updatePrice(
            int id,
            int price) {

        String sql = """
                UPDATE items
                SET price = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                price,
                id);
    }

    // 在庫数変更
    public void updateStock(
            int id,
            int stock) {

        String sql = """
                UPDATE items
                SET stock = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                stock,
                id);
    }

    // 商品を販売停止にする
    public void stopSelling(
            int id) {

        String sql = """
                UPDATE items
                SET active = FALSE
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                id);
    }

    // 販売を再開する
    public void restartSelling(
            int id) {

        String sql = """
                UPDATE items
                SET active = TRUE
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                id);
    }
}