package service.entity;


import java.util.ArrayList;

public class AccountDAO {

    public static Account getAccount(String id, ArrayList<Account> accountList){
        return accountList.stream().filter(account -> id.equals(account.getId())).findFirst().orElse(null);
    }

    public static Account createNewAcc(String name, int fund,ArrayList<Account> accountList){
        Account result = new Account(name,fund);
        accountList.add(result);
        accountList.toString();
        return result;
    }

}
