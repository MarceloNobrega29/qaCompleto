package com.example.demo.service;

import com.example.demo.exception.InvalidItemDataException;
import com.example.demo.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private List<Item> items = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public List<Item> getAllItems() {
        return items;
    }

    public Optional<Item> getItemById(Long id) {
        return items.stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst();
    }

    public Item createItem(Item item) {
        item.setId(counter.incrementAndGet());
        items.add(item);
        return item;
    }

    //NOVOS
    public Optional<Item> updateItem(Long id, Item newItemData) {
        return getItemById(id).map(existingItem -> {
            existingItem.setName(newItemData.getName());
            existingItem.setDescription(newItemData.getDescription());
            return existingItem;
        });
    }

    public boolean deleteItem(Long id) {
        if (getItemById(id).isPresent()) {
            items.removeIf(item -> id.equals(item.getId()));
            return true;
        }
        return false;
    }

    public Item cloneItem(Long id) {
        Item existingItem = getItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado para clonagem"));

        if (existingItem.getName().contains("(Clone)")) {
            throw new InvalidItemDataException("Não é permitido clonar um item que já é um clone.");
        }

        Item clonedItem = new Item();
        clonedItem.setId(counter.incrementAndGet());
        clonedItem.setName(existingItem.getName() + " (Clone)");
        clonedItem.setDescription(existingItem.getDescription());
        items.add(clonedItem);

        return clonedItem;
    }

    // NOVO: Regra para alterar apenas o nome
    public Item updateItemName(Long id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new InvalidItemDataException("O novo nome não pode ser vazio.");
        }

        Item existingItem = getItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

        if (existingItem.getName().equals(newName)) {
            throw new InvalidItemDataException("O novo nome deve ser diferente do nome atual.");
        }

        existingItem.setName(newName);
        return existingItem;
    }
}