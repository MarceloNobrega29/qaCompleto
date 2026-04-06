package com.example.demo.service;

import com.example.demo.exception.InvalidItemDataException;
import com.example.demo.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {

    private ItemService itemService;

    // O metodo anotado com @BeforeEach roda antes de CADA teste
    @BeforeEach
    void setUp() {
        // Criamos uma nova instância do serviço para cada teste, garantindo isolamento
        itemService = new ItemService();
    }

    @Test
    void whenCreateItem_thenItemIsStoredAndIdIsAssigned() {
        // Cenário (Given)
        Item newItem = new Item(null, "Test Item", "Test Description");

        // Ação (When)
        Item savedItem = itemService.createItem(newItem);

        // Verificação (Then)
        assertNotNull(savedItem.getId(), "O ID não deveria ser nulo após salvar");
        assertEquals("Test Item", savedItem.getName());

        // Verifica se o item foi realmente armazenado
        Optional<Item> foundItem = itemService.getItemById(savedItem.getId());
        assertTrue(foundItem.isPresent(), "O item deveria ser encontrado pelo ID");
        assertEquals("Test Item", foundItem.get().getName());
    }

    @Test
    void whenGetItemByNonExistentId_thenReturnsEmptyOptional() {
        // Ação (When)
        Optional<Item> foundItem = itemService.getItemById(999L);

        // Verificação (Then)
        assertFalse(foundItem.isPresent(), "Nenhum item deveria ser encontrado para um ID inexistente");
    }

    @Test
    void givenItemsExist_whenGetAllItems_thenReturnsAllItems() {
        // Cenário (Given)
        itemService.createItem(new Item(null, "Item 1", "Desc 1"));
        itemService.createItem(new Item(null, "Item 2", "Desc 2"));

        // Ação (When)
        List<Item> allItems = itemService.getAllItems();

        // Verificação (Then)
        assertEquals(2, allItems.size(), "A lista deveria conter 2 itens");
    }

    // NOVOS

    // Em src/test/java/com/example/demo/service/ItemServiceTest.java

//... (imports e a classe de teste)

    @Test
    void whenUpdateItem_thatExists_thenItemIsUpdated() {
        // Cenário (Given)
        Item originalItem = itemService.createItem(new Item(null, "Original Name", "Original Desc"));
        Item updatedData = new Item(null, "Updated Name", "Updated Desc");

        // Ação (When)
        Optional<Item> result = itemService.updateItem(originalItem.getId(), updatedData);

        // Verificação (Then)
        assertTrue(result.isPresent(), "O item atualizado deveria ser retornado");
        assertEquals("Updated Name", result.get().getName());
        assertEquals(originalItem.getId(), result.get().getId(), "O ID não deve mudar na atualização");
    }

    @Test
    void whenDeleteItem_thatExists_thenItemIsRemoved() {
        // Cenário (Given)
        Item itemToDelete = itemService.createItem(new Item(null, "To Be Deleted", "Desc"));
        Long id = itemToDelete.getId();

        // Ação (When)
        boolean wasDeleted = itemService.deleteItem(id);

        // Verificação (Then)
        assertTrue(wasDeleted, "O método deveria retornar true para uma remoção bem-sucedida");
        assertFalse(itemService.getItemById(id).isPresent(), "O item não deveria mais ser encontrado após a remoção");
    }

    @Test
    void whenCloneItem_ValidItem_thenCreatesClone() {
        Item original = itemService.createItem(new Item(null, "Cadeira", "Cadeira Gamer"));

        Item cloned = itemService.cloneItem(original.getId());

        assertNotNull(cloned.getId());
        assertNotEquals(original.getId(), cloned.getId());
        assertEquals("Cadeira (Clone)", cloned.getName());
    }

    @Test
    void whenCloneItem_AlreadyAClone_thenThrowsException() {
        Item original = itemService.createItem(new Item(null, "Mesa (Clone)", "Mesa de Escritório"));

        Exception exception = assertThrows(InvalidItemDataException.class, () -> {
            itemService.cloneItem(original.getId());
        });

        assertEquals("Não é permitido clonar um item que já é um clone.", exception.getMessage());
    }

    @Test
    void whenUpdateItemName_ValidName_thenNameIsUpdated() {
        Item item = itemService.createItem(new Item(null, "Teclado", "Teclado Mecânico"));

        Item updated = itemService.updateItemName(item.getId(), "Teclado RGB");

        assertEquals("Teclado RGB", updated.getName());
    }

    @Test
    void whenUpdateItemName_SameName_thenThrowsException() {
        Item item = itemService.createItem(new Item(null, "Mouse", "Mouse Óptico"));

        Exception exception = assertThrows(InvalidItemDataException.class, () -> {
            itemService.updateItemName(item.getId(), "Mouse");
        });

        assertEquals("O novo nome deve ser diferente do nome atual.", exception.getMessage());
    }

    @Test
    void whenUpdateItemName_EmptyName_thenThrowsException() {
        Item item = itemService.createItem(new Item(null, "Monitor", "Monitor 24"));

        assertThrows(InvalidItemDataException.class, () -> {
            itemService.updateItemName(item.getId(), "   ");
        });
    }
}