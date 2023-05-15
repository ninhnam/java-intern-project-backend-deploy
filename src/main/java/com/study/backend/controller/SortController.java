package com.study.backend.controller;

import com.study.backend.entity.Sort;
import com.study.backend.entity.SortInfo;
import com.study.backend.exception.BadRequestException;
import com.study.backend.exception.ForbiddenException;
import com.study.backend.exception.NotFoundException;
import com.study.backend.request.ResponseData;
import com.study.backend.request.SortRaw;
import com.study.backend.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sorts")
public class SortController {

    @Autowired
    private SortService sortService;

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseData<String>> handleBadRequestException(BadRequestException ex) {
        ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseData<String>> handleNotFoundException(NotFoundException ex) {
        ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseData<String>> handleForbiddenException(NotFoundException ex) {
        ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<ResponseData<Sort>> createSort(@RequestBody SortRaw sortRaw) {
        Sort createdSort = sortService.createSort(sortRaw);
        ResponseData<Sort> response = new ResponseData<>("Create success", createdSort);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Sort>> getSortById(@PathVariable Long id) {
        Sort sort = sortService.getSortById(id);
        ResponseData<Sort> response = new ResponseData<>("Success", sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseData<List<SortInfo>>> getSortByUserId(@PathVariable Long id) {
        List<SortInfo> sorts = sortService.getSortsByUserId(id);
        ResponseData<List<SortInfo>> response = new ResponseData<>("Success", sorts);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/{quantity}")
    public ResponseEntity<ResponseData<String>> updateSort(@PathVariable Long id, @PathVariable int quantity) {
        Sort existingSort = sortService.getSortById(id);

        existingSort.setQuantity(quantity);
        sortService.updateSort(existingSort);

        ResponseData<String> response = new ResponseData<>("Update success", "Update sort with id = " + id + " success!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteSortById(@PathVariable Long id) {
        sortService.deleteSortById(id);

        ResponseData<String> response = new ResponseData<>("Delete success", "Delete sort with id = " + id);
        return ResponseEntity.ok(response);
    }
}
