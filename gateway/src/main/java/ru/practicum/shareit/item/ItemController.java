package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Create item {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemDto itemDto) {
        log.info("Update item {}", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    ResponseEntity<Object> getItemByUserId(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item by userId={}", userId);
        return itemClient.getItemByUserId(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemListByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all items from user {}", userId);
        return itemClient.getItemListByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items by text {}", text);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                              @RequestBody @Valid CommentDto commentDto) {
        log.info("Create comment from user {}", userId);
        return itemClient.saveComment(userId, itemId, commentDto);
    }
}
