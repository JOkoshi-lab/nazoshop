package com.example.demo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.NazoShop.NazoItem;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class NazoSession implements Serializable {

    private List<NazoItem> cart;

    public List<NazoItem> getCart() {
        if (cart == null) {
            cart = new ArrayList<>();
        }

        return cart;
    }

    public void addItem(NazoItem newItem) {
        List<NazoItem> cart = getCart();

        for (int i = 0; i < cart.size(); i++) {
            NazoItem oldItem = cart.get(i);

            if (oldItem.id() == newItem.id()) {
                int newQuantity = oldItem.quantity() + newItem.quantity();

                NazoItem updatedItem = new NazoItem(
                        oldItem.id(),
                        oldItem.name(),
                        oldItem.price(),
                        newQuantity);

                cart.set(i, updatedItem);
                return;
            }
        }

        cart.add(newItem);
    }

    // カート内に入っている商品の個数を取得する
    public int getItemQuantity(
            int itemId) {

        List<NazoItem> cart = getCart();

        for (NazoItem item : cart) {

            if (item.id() == itemId) {
                return item.quantity();
            }
        }

        return 0;
    }

    // カート内の商品個数を変更する
    public void updateItemQuantity(
            int itemId,
            int quantity) {

        List<NazoItem> cart = getCart();

        // 個数が0以下の場合はカートから削除する
        if (quantity <= 0) {
            removeItem(itemId);
            return;
        }

        for (int i = 0; i < cart.size(); i++) {

            NazoItem oldItem = cart.get(i);

            if (oldItem.id() == itemId) {

                NazoItem updatedItem = new NazoItem(
                        oldItem.id(),
                        oldItem.name(),
                        oldItem.price(),
                        quantity);

                cart.set(i, updatedItem);
                return;
            }
        }
    }

    // カートから特定の商品を削除する
    public void removeItem(int itemId) {

        List<NazoItem> cart = getCart();

        cart.removeIf(
                item -> item.id() == itemId);
    }

    public void clearCart() {
        cart = new ArrayList<>();
    }
}