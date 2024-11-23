import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VotingProcessor {
    private static final String ENCRYPTED_VOTE_FILE = "encryptedVotes.txt";
    private static final String DECRYPTED_VOTE_FILE = "decryptedVotes.txt";
    private static final String USERS_FILE = "users.txt";

    private static final String NAWKA = "NAWKA";
    private static final String DHANER_SHISH = "DHANER SHISH";

    private static final SecureRandom random = new SecureRandom();
    private static final BigInteger ONE = BigInteger.ONE;

    private BigInteger modulus;
    private BigInteger publicKey;
    private BigInteger privateKey;

    public void generateKeys() 
    {
        BigInteger p = BigInteger.probablePrime(512, random);
        BigInteger q = BigInteger.probablePrime(512, random);

        this.modulus = p.multiply(q);
        BigInteger phi = p.subtract(ONE).multiply(q.subtract(ONE));

        this.publicKey = BigInteger.valueOf(65537);
        this.privateKey = publicKey.modInverse(phi);
    }

    public String encrypt(String message) 
    {
        StringBuilder encryptedMessage = new StringBuilder();

        for (int i = 0; i < message.length(); i++) 
        {
            char character = message.charAt(i);
            BigInteger charValue = BigInteger.valueOf(character);
            BigInteger encryptedValue = charValue.modPow(publicKey, modulus);
            encryptedMessage.append(encryptedValue).append(" ");
        }

        return encryptedMessage.toString();
    }

    public String decrypt(String encryptedMessage) 
    {
        StringBuilder decryptedMessage = new StringBuilder();

        String[] encryptedChars = encryptedMessage.trim().split("\\s+");
        for (String encryptedChar : encryptedChars) 
        {
            BigInteger encryptedValue = new BigInteger(encryptedChar);
            BigInteger decryptedValue = encryptedValue.modPow(privateKey, modulus);
            decryptedMessage.append((char) decryptedValue.intValue());
        }

        return decryptedMessage.toString();
    }

    public void userLogin(Scanner scanner) 
    {
        System.out.println("=== User Login ===");
        System.out.print("Enter your name: ");
        scanner.nextLine(); 
        String name = scanner.nextLine();
        System.out.print("Enter your NID card number: ");
        String nid = scanner.nextLine();

        if (!isUserRegistered(name, nid)) 
        {
            System.out.println("You are not registered to vote. Access denied!");
            return;
        }

        if (hasUserVoted(nid)) 
        {
            System.out.println("You have already voted. You cannot vote again!");
            return;
        }

        displayVotingOptions(scanner);

        System.out.print("Enter your vote choice: ");
        int choice = scanner.nextInt();

        switch (choice) 
        {
            case 1:
                recordVote(NAWKA, nid);
                break;
            case 2:
                recordVote(DHANER_SHISH, nid);
                break;
            default:
                System.out.println("Invalid choice. Vote not recorded.");
        }
    }

    public void displayTotalVotes() 
    {
    Map<String, Integer> voteCounts = new HashMap<>();
    voteCounts.put(NAWKA, 0); 
    voteCounts.put(DHANER_SHISH, 0);

        try (BufferedReader reader = new BufferedReader(new FileReader(DECRYPTED_VOTE_FILE))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] parts = line.split(",");
                if (parts.length == 2) 
                {
                    String vote = parts[0].trim();
                    if (vote.equals(NAWKA)) 
                    {
                        voteCounts.put(NAWKA, voteCounts.get(NAWKA) + 1);
                    } 
                    else if (vote.equals(DHANER_SHISH)) 
                    {
                        voteCounts.put(DHANER_SHISH, voteCounts.get(DHANER_SHISH) + 1);
                    }
                }
            }

            System.out.println("Total Votes:");
            System.out.println(NAWKA + ": " + voteCounts.get(NAWKA));
            System.out.println(DHANER_SHISH + ": " + voteCounts.get(DHANER_SHISH));
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while displaying total votes.");
        }
    }


    private void displayVotingOptions(Scanner scanner) 
    {
        System.out.println("===> Voting Options <===");
        System.out.println("1. NAWKA");
        System.out.println("2. DHANER SHISH");
    }

    private void recordVote(String vote, String nid) 
    {
        try {
            String encryptedVote = encrypt(vote);
            String decryptedVote = decrypt(encryptedVote);
            saveToFile(encryptedVote + "," + nid, ENCRYPTED_VOTE_FILE);
            saveToFile(decryptedVote + "," + nid, DECRYPTED_VOTE_FILE);
            System.out.println("Vote recorded successfully!");
        } 
        catch (Exception e) 
        {
            System.out.println("An error occurred while recording vote!");
        }
    }

    private void saveToFile(String data, String fileName) 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) 
        {
            writer.write(data + "\n");
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while saving to file: " + fileName);
        }
    }

    private boolean isUserRegistered(String name, String nid) 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] userInfo = line.split(",");
                if (userInfo.length == 2 && userInfo[0].trim().equals(name) && userInfo[1].trim().equals(nid)) 
                {
                    return true;
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while checking user registration!");
        }
        return false; 
    }

    private boolean hasUserVoted(String nid) 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(ENCRYPTED_VOTE_FILE))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[1].trim().equals(nid)) 
                {
                    return true;
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while checking if user has voted.");
        }
        return false; 
    }
}
