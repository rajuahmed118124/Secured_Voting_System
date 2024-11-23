import java.util.Scanner;

import java.util.Calendar;

public class VotingManager {
    private static final String NAWKA = "NAWKA";
    private static final String DHANER_SHISH = "DHANER SHISH";

    private VotingProcessor votingProcessor;

    public VotingManager() 
    {
        votingProcessor = new VotingProcessor();
    }

    public void startVoting() 
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 8 && hour <= 16) 
        {
            votingProcessor.generateKeys();
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) 
            {
                System.out.println("===> Main Menu <===");
                System.out.println("1. Log in");
                System.out.println("2. Show Results");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int option = scanner.nextInt();

                switch (option) 
                {
                    case 1:
                        votingProcessor.userLogin(scanner);
                        break;
                    case 2:
                        votingProcessor.displayTotalVotes();
                        break;
                    case 3:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }

            scanner.close();
        } 
        else {
            System.out.println("Voting is only allowed between 8 am to 4 pm.");
        }
    }
}

