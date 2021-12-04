package com.bluenexus.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class User extends Base{

    private String name;
    private String email;
    private String password;
    @OneToOne
    private Wallet wallet;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
}