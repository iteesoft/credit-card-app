package com.bluenexus.controller;

import com.bluenexus.dto.UserDto;
import com.bluenexus.service.CardService;
import com.bluenexus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/credit")
public class AppController {

    private final CardService cardService;
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto registrationInfo) {
        return new ResponseEntity<>(userService.save(registrationInfo), HttpStatus.CREATED);
    }

    @GetMapping("/wallet/{userId}")
    public ResponseEntity<?> viewWallet(@PathVariable int userId) {
        return new ResponseEntity<>(userService.viewWallet(userId), HttpStatus.OK);
    }

    @PostMapping("/limit/{userId}")
    public ResponseEntity<?> setCreditLimit(@PathVariable int userId, @RequestBody String amount) {
        return new ResponseEntity<>(cardService.setCreditLimit(userId, amount), HttpStatus.OK);
    }

    @PostMapping("/purchase/{userId}")
    public ResponseEntity<?> purchase(@PathVariable int userId, @RequestBody String amount) {
        return new ResponseEntity<>(cardService.makePurchase(userId, amount), HttpStatus.OK);
    }

    @GetMapping("/statement/{userId}")
    public ResponseEntity<?> viewTransactions(@PathVariable int userId) {
        return new ResponseEntity<>(cardService.getAllTransactions(userId), HttpStatus.OK);
    }

    @GetMapping("/statement/{userId}/month/{month}")
    public ResponseEntity<?> viewMonthlyTransactions(@PathVariable int userId, @PathVariable int month) {
        return new ResponseEntity<>(cardService.getMonthlyTransactions(userId, month), HttpStatus.OK);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetLimit() {
        cardService.resetAllCreditLimit();
        return ResponseEntity.ok().build();
    }
}