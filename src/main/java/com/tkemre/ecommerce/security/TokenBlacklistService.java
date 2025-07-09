package com.tkemre.ecommerce.security;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Date> tokenBlacklist = new ConcurrentHashMap<>(); //Blacklist

    public void blacklistToken(String token,Date expirationDate){ //tokeni kara listeye ekler
        tokenBlacklist.put(token,expirationDate);
    }
    //Bir tokenin kara listede olup olmadığını kontrol eder.
    //@param token kontrol edilecek jwt token strringi
    //@return Token kara listedeyse ve süresi dolmamışsa true.
    public boolean isBlacklisted(String token){
        Date experiationDate = tokenBlacklist.get(token);
        return experiationDate != null && experiationDate.after(new Date());
    }
    public void cleanUpBlacklist(){
        Date now = new Date();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
        System.out.println("Blacklist temizlendi. Kalan token sayısı " + tokenBlacklist.size());
    }

}

