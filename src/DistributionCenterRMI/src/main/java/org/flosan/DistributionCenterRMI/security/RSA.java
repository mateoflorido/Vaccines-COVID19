package org.flosan.DistributionCenterRMI.security;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RSA {
    public static PrivateKey generateRSAPrivate() {
        String privKey = "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQCyhmVukWbGgQ3p" +
                "+3kxpE6WzYdcQXNmY4zFlusXtdEoPVyh3/hDtE8YS2cP7cNzv/aiAHeL0v54s7EO" +
                "DqCvQKIyfEXXR1OWfDYi9NkTqDMzPpSWx7UiYIhay6Orl+ANfcoLReNZAPqgerPx" +
                "kEiZtO/nsIop5GJYgvoIJ+NhJZJIuLH9yTllcYFVwn00LDnwsRMBKzgwkSeIN87t" +
                "TMIwZKCI+YZ3nXDaZ5R++yuXP9xhWKAtHzuBvDhcdCs7njsxJqEdlNsIFpoW9+GU" +
                "80DNxWpMZzIeNwZfulH2tWRKvgpsriBTZY9CTQ3bN86O4uStZUceMW2+eXpjZojE" +
                "OOCUd/Ke3NE1A0KX0hzZNnqPiE9kcN0YKlUEA6/GAvq2fuq5gI/Y6hWDe3vJJYtx" +
                "0C7SesiFZqpYLzAOLvLOeGAiu8Qj+B4vmkTQAenYiErhbcrGqHnQ3JSSxdBrnW43" +
                "P03u8BCW9+EzIZqMupyNcoWWMoam1VPsmbJ9nAiNSyowD3OPAnNJ4LryKEkDa+8z" +
                "AKRGRnxcnSgniiNg6nNrcQ1xQ0UImhNLBXuN8CrISnQ8HJvmR9kEp0DZYcE46rrO" +
                "bE2G3tXq+28FVsnzTNBwhFYqG1UV0vzC9jNeYB3Zd7fXlIP48iizZchqhJx9V0eI" +
                "esUEryRmG5RaIc+fQr7noCugb68Y4wIDAQABAoICAQChP8lAXG7jX0/tBupJz2n4" +
                "xj28M5YDZmmRUyaXbO81lG8l+0GoDaXfgMPfXFfNJhej7h4MmpCk/1EuLEIw6YVX" +
                "am4+67rxUtInMtcb7TIJ2D2oD0xpd53RvNvhsrV6GiMzmXWnP74g05PS3grlYsvK" +
                "jufMvO+jmPI0CatUZPZDXyfN3kJMgYBbsOccB9Jxt889oAcJvyJg5F+wcUN5KHrk" +
                "HdW6FugHLcWwCDgwTktFViKffvVl0ldqEre2Cs7WL1kZegl4vcmCc3U675iaA/Vm" +
                "e1OGfRbxwj4kQ2UKKasKzSB/k10ZerXISxj0cLYydCcPe3/vqpPEUqm36tPW5pyq" +
                "I5//rR35StxpH3vBYvIRCzqEgZlG/FiAP0jU7ib+c9gooyawE+Vjpv06zwG5cgh6" +
                "quOX9+TBzi4yoSJbiomQ7Os3tDAy/eDQYUSBrVvWGp1/EelrRqqu0o66tkB/mEeL" +
                "nbYUthePQKsT43qt88CP2l717zS4SI/dHjWT5+YP8eR5CmhIFsGiCOhxrOFhhAFF" +
                "BKlZaaCg4rp4JU0mILEfS9I4f9NtHFOOw938YJ50wgfYbpO7Q/cdYP7w3+2JqEUN" +
                "ndGwrxrxU+RVngo//4kqpLOdCWxRfnqiNy9o/6Stp7A96VCKI2UsQY2Oj4kygqU4" +
                "O+eGjQyi/YZ2aD4aUYPqQQKCAQEA6Kf6wIutD8W4dl6yfbtNbFeVnAK0V7Ib3tyT" +
                "m7vHVdbL5gDEt4MOZohuKxmb/V5+6R/99OvrBZ+IElGQKr02svWXOAv1oOa2t0nW" +
                "flIY7WoQRnUwf2OSzaI2au1apUEhxzI0INqDsW5yqf4+nUioWsnjqI6FWczzcimG" +
                "6mxbZ550R27YIR0hxYLwDvBnwxPFAwvwOASGckVa7QO9lj8Qm54mNa7b0FOhjIQw" +
                "csz7J8UunNHRH5AGZI8Z34LFLlvm8kHKCkKsaJe1uwD8lw4ymnj5U3r77aXms31L" +
                "rCffvcBfewvvvrMxSjcJULEzw2oLxF3lWrMaPXFIGEypYkp0wwKCAQEAxG//9GMj" +
                "KrJGcMgWC92CnVqHLbRe+51t1qqgkTsYgYZ/WcpoN6ELRS1YXU/1HVdtGKyw7CwP" +
                "DC1Hcrg8Xx++hcF/DEK5vGF7lE67T1E49TM3UsWnh8Ipi0G+/rO8LOv+FkzKCsK4" +
                "LRhf/kTmaaJUHLUMBlU+P488zlSfwyf990P3WWYJ/NG+KwHVK02Lml+j1jr/uQE0" +
                "lxHVX0+1RJ6x+AZIh3TdrwhHh6e+jS33AMx47sSZ63Rd8G5o650GWVXbhuuHXeXi" +
                "41l3IARy2vpB9CLSEZCZB6lAR1m/lC0/cA3ZLyWFH1Krbc3iD5tmNJiGk8+x+41B" +
                "ZRSFL55/sUoJYQKCAQEAmGFNDsds9iOeCYlEhq0irI9A4gmzbKyvLGx0EVAP4tzo" +
                "dyCRt1ATInzBC2GelXhKzw2xG7VZHTm7uaPKNPPw242XGZJbsqLpkWq/HcVPplAN" +
                "kNg4h3cEM8LGuYn0EMB0Cy+KjOoRzyhz2Xs4L3PjMHOCtGeEV6iw2ljwkKGfzGIv" +
                "7ole7XFTKTX1XROAInjUVIaBM29jT3y1bP9CMSHlEsemZwq71dqwuIPr54TyCr5P" +
                "JU8XFQET7tD7NTVFSHPQ82K7nbTFj0uuL2kaByW5ZwtM4axvTNuYGn86DOug03so" +
                "DkNnJxPeeKlLZJprONZAFsEY8yrTSOLlQWUUm9gAWQKCAQEAmZNKk6EyLD5GjgQQ" +
                "IGSkqirNhnD+1U6GQg8SC5kdFp61T9jL07oHWqUR8ZoCc1cXhvP0OiBT/1wjANCC" +
                "+oeR53aYwlgkZ9/6wr8u99rzYdgRx5eZ26xz2FilXvHcC27RLlQInZMQdMkau2rY" +
                "jPuJMQxmg0j/qqdsjY9hfCz3A+rSJcV9x3efanY7V2h/3UgqieGEbfpASF975tEl" +
                "UamUT+lxqN04N5CMg8UkV7H461nwrhNslsVacA+R3arc/NkAESJwqXuB/+bBGHoB" +
                "5atEBv92Jp9a1L+A7od/HPSnJQ7enlqCEjQKrIUK2NLQfAxHIqZ9asJLvx/k5I7R" +
                "HCVa4QKCAQB2KOpOMtGC1BLYgIIFzgbj8IhljcrasLctw2EixPALT4PYHsyOAdSi" +
                "PYvLOo/6xGj3om6p4cd7SsgfQdNAUrICrpf0YU8pdHvzYXXMTen76LrLtVBApQX+" +
                "Qe/fOs2nZkRn8sEef2Ig9L91qjfCyFpHmFf7DSKWQOTBMN2KwGbtBndVJEd67uyT" +
                "/SMQB3JFVtfdGuIENp4LxZTk+Pre0B/hIpEDJe7fEmfKqp7hcaqUtECyrwlcFdPo" +
                "wctynfyWNR2/C+5/MIli8QzmGKuPNbpEV79rnD5itd0nZ5JKud1bY+ZsarIDjwho" +
                "peiRxqxk0SXep0Sn0OlAa7K7CkegIN4i";
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privKey));
            return kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static SecretKey Decrypt(PrivateKey pk, SealedObject so) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pk);
            return (SecretKey) so.getObject(cipher);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("ERROR: No Algorithm to decrypt!");
        } catch (NoSuchPaddingException e) {
            System.err.println("ERROR: No Such Padding!");
        } catch (InvalidKeyException e) {
            System.err.println("ERROR: No valid Key!");
        } catch (IOException | ClassNotFoundException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
