package com.example.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.EmptyResultDataAccessException;

@SpringBootApplication
@Controller
public class NazoShop {

    private final NazoSession nazoSession;
    private final ItemDao itemDao;
    private final OrderDao orderDao;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    record NazoItem(
            int id,
            String name,
            int price,
            int quantity) {
    }

    public static void main(String[] args) {
        SpringApplication.run(NazoShop.class, args);
    }

    NazoShop(
            NazoSession nazoSession,
            ItemDao itemDao,
            OrderDao orderDao) {

        this.nazoSession = nazoSession;
        this.itemDao = itemDao;
        this.orderDao = orderDao;
    }

    // ショップのトップページ
    @GetMapping("/nazoshop")
    String home(
            Model model,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "searchCompany", required = false) String searchCompany) {

        // 商品名と会社名を使って検索する
        List<Map<String, Object>> itemList = itemDao.searchItems(
                keyword,
                searchCompany);

        // 検索結果の商品一覧
        model.addAttribute(
                "items",
                itemList);

        // 検索後も入力した商品名を画面に残す
        model.addAttribute(
                "selectedKeyword",
                keyword);

        // 検索後も選択した会社を画面に残す
        model.addAttribute(
                "selectedCompany",
                searchCompany);

        return "nazoshop";
    }

    // カートへ商品を追加
    @PostMapping("/addItem")
    String addItem(
            @RequestParam("itemId") int itemId,
            @RequestParam("quantity") int quantity) {

        if (quantity <= 0 || quantity > 99) {
            return "redirect:/nazoshop";
        }

        Map<String, Object> item;

        try {
            item = itemDao.getItemById(itemId);

        } catch (EmptyResultDataAccessException e) {
            return "redirect:/nazoshop";
        }

        int id = ((Number) item.get("id")).intValue();

        String name = item.get("name").toString();

        int price = ((Number) item.get("price")).intValue();

        int stock = ((Number) item.get("stock")).intValue();

        int currentCartQuantity = nazoSession.getItemQuantity(itemId);

        int afterQuantity = currentCartQuantity + quantity;

        if (afterQuantity > stock) {
            return "redirect:/nazoshop?stockError=true";
        }

        NazoItem nazoItem = new NazoItem(
                id,
                name,
                price,
                quantity);

        nazoSession.addItem(nazoItem);

        return "redirect:/nazocart";
    }

    // カートから商品を削除
    @PostMapping("/removeItem")
    String removeItem(@RequestParam("itemId") int itemId) {
        nazoSession.removeItem(itemId);
        return "redirect:/nazocart";
    }

    // カート内の商品個数を変更
    @PostMapping("/updateQuantity")
    String updateQuantity(
            @RequestParam("itemId") int itemId,
            @RequestParam("quantity") int quantity) {

        if (quantity <= 0) {

            nazoSession.removeItem(itemId);

            return "redirect:/nazocart";
        }

        if (quantity > 99) {
            return "redirect:/nazocart?stockError=true";
        }

        int stock = itemDao.getStockById(itemId);

        if (quantity > stock) {
            return "redirect:/nazocart?stockError=true";
        }

        nazoSession.updateItemQuantity(
                itemId,
                quantity);

        return "redirect:/nazocart";
    }

    // カート画面
    @GetMapping("/nazocart")
    String cart(Model model) {

        List<NazoItem> cart = nazoSession.getCart();

        int totalPrice = 0;

        // 商品IDと現在庫を保存するMap
        Map<Integer, Integer> stockMap = new HashMap<>();

        for (NazoItem item : cart) {

            totalPrice += item.price() * item.quantity();

            int stock = itemDao.getStockById(item.id());

            stockMap.put(
                    item.id(),
                    stock);
        }

        model.addAttribute(
                "cart",
                cart);

        model.addAttribute(
                "totalPrice",
                totalPrice);

        model.addAttribute(
                "stockMap",
                stockMap);

        return "nazocart";
    }

    // 顧客情報入力画面
    @GetMapping("/checkout")
    String checkout(Model model) {

        List<NazoItem> cart = nazoSession.getCart();

        if (cart.isEmpty()) {
            return "redirect:/nazocart";
        }

        int totalPrice = 0;

        for (NazoItem item : cart) {

            totalPrice += item.price() * item.quantity();
        }

        model.addAttribute(
                "cart",
                cart);

        model.addAttribute(
                "totalPrice",
                totalPrice);

        return "checkout";
    }

    // 購入処理
    @PostMapping("/buy")
    String buy(
            @RequestParam("customerName") String customerName,

            @RequestParam("email") String email,

            @RequestParam("address") String address,

            RedirectAttributes redirectAttributes) {

        List<NazoItem> cart = nazoSession.getCart();

        if (cart.isEmpty()) {
            return "redirect:/nazocart";
        }

        String safeCustomerName = customerName.trim();

        String safeEmail = email.trim();

        String safeAddress = address.trim();

        if (safeCustomerName.isBlank()
                || safeEmail.isBlank()
                || safeAddress.isBlank()
                || !safeEmail.contains("@")
                || safeCustomerName.length() > 100
                || safeEmail.length() > 255
                || safeAddress.length() > 255) {

            return "redirect:/checkout?inputError=true";
        }

        try {

            String orderId = orderDao.saveOrder(
                    cart,
                    safeCustomerName,
                    safeEmail,
                    safeAddress);

            nazoSession.clearCart();

            redirectAttributes.addAttribute(
                    "orderId",
                    orderId);

            return "redirect:/buy-complete";

        } catch (IllegalStateException e) {

            return "redirect:/nazocart?stockError=true";
        }
    }

    // 購入完了画面
    @GetMapping("/buy-complete")
    String buyComplete(
            @RequestParam(name = "orderId", required = false) String orderId,

            Model model) {

        if (orderId == null
                || orderId.isBlank()) {

            return "redirect:/nazoshop";
        }

        model.addAttribute(
                "orderId",
                orderId);

        return "buy-complete";
    }

    // 管理者ログイン画面
    @GetMapping("/admin/login")
    String adminLogin() {
        return "admin-login";
    }

    // 管理者ログイン処理
    @PostMapping("/admin/login")
    String adminLoginProcess(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        if (adminUsername.equals(username)
                && adminPassword.equals(password)) {

            session.setAttribute(
                    "adminLoggedIn",
                    true);

            return "redirect:/admin";
        }

        model.addAttribute(
                "error",
                "ユーザー名またはパスワードが違います。");

        return "admin-login";
    }

    // 管理者トップページ
    @GetMapping("/admin")
    String admin(
            Model model,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "items",
                itemDao.getAllItemsForAdmin());

        model.addAttribute(
                "orderHistory",
                orderDao.getOrderHistory());

        model.addAttribute(
                "totalSales",
                orderDao.getTotalSales());

        model.addAttribute(
                "orderCount",
                orderDao.getOrderCount());

        return "admin";
    }

    // 商品追加
    @PostMapping("/admin/add-item")
    String adminAddItem(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("company") String company,
            @RequestParam("price") int price,
            @RequestParam("stock") int stock,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        if (name.isBlank()
                || price < 0
                || stock < 0) {

            return "redirect:/admin";
        }

        itemDao.addItem(
                name,
                category,
                company,
                price,
                stock);

        return "redirect:/admin";
    }

    // 商品価格の変更
    @PostMapping("/admin/update-price")
    String adminUpdatePrice(
            @RequestParam("id") int id,
            @RequestParam("price") int price,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        if (price >= 0) {
            itemDao.updatePrice(id, price);
        }

        return "redirect:/admin";
    }

    // 商品在庫の変更
    @PostMapping("/admin/update-stock")
    String adminUpdateStock(
            @RequestParam("id") int id,
            @RequestParam("stock") int stock,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        if (stock >= 0) {

            itemDao.updateStock(
                    id,
                    stock);
        }

        return "redirect:/admin";
    }

    // 商品を販売停止にする
    @PostMapping("/admin/delete-item")
    String adminDeleteItem(
            @RequestParam("id") int id,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        itemDao.stopSelling(id);

        return "redirect:/admin";
    }

    // 商品の販売を再開する
    @PostMapping("/admin/restart-item")
    String adminRestartItem(
            @RequestParam("id") int id,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        itemDao.restartSelling(id);

        return "redirect:/admin";
    }

    // ログアウト
    @PostMapping("/admin/logout")
    String adminLogout(HttpSession session) {

        session.removeAttribute("adminLoggedIn");

        return "redirect:/admin/login";
    }

    // ログイン済みか確認する
    private boolean isAdminLoggedIn(
            HttpSession session) {

        Object value = session.getAttribute("adminLoggedIn");

        return Boolean.TRUE.equals(value);
    }
}