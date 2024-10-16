package activity;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class FraudDetectionSystemTest {
    private FraudDetectionSystem fraudSystem;

    @BeforeEach
    void setUp() {
        fraudSystem = new FraudDetectionSystem();
    }

    @Test
    void testLargeAmountTransaction() {
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(15000, LocalDateTime.now(), "New York");
        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(transaction, new ArrayList<>(), new ArrayList<>());

        Assertions.assertTrue(result.isFraudulent);
        Assertions.assertTrue(result.verificationRequired);
        Assertions.assertEquals(50, result.riskScore);
    }

    @Test
    void testExcessiveTransactions() {
        List<FraudDetectionSystem.Transaction> previousTransactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 11; i++) {
            previousTransactions.add(new FraudDetectionSystem.Transaction(100, now.minusMinutes(i * 5), "New York"));
        }

        FraudDetectionSystem.Transaction currentTransaction = new FraudDetectionSystem.Transaction(100, now, "New York");
        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, new ArrayList<>());

        Assertions.assertTrue(result.isBlocked);
        Assertions.assertEquals(30, result.riskScore);
    }

    @Test
    void testQuickLocationChange() {
        List<FraudDetectionSystem.Transaction> previousTransactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        previousTransactions.add(new FraudDetectionSystem.Transaction(100, now.minusMinutes(20), "New York"));

        FraudDetectionSystem.Transaction currentTransaction = new FraudDetectionSystem.Transaction(100, now, "Los Angeles");
        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, new ArrayList<>());

        Assertions.assertTrue(result.isFraudulent);
        Assertions.assertTrue(result.verificationRequired);
        Assertions.assertEquals(20, result.riskScore);
    }

    @Test
    void testBlacklistedLocation() {
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(100, LocalDateTime.now(), "Suspicious City");
        List<String> blacklistedLocations = Arrays.asList("Suspicious City", "Fraud Town");

        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(transaction, new ArrayList<>(), blacklistedLocations);

        Assertions.assertTrue(result.isBlocked);
        Assertions.assertEquals(100, result.riskScore);
    }

    @Test
    void testNormalTransaction() {
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(500, LocalDateTime.now(), "Safe City");
        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(transaction, new ArrayList<>(), new ArrayList<>());

        Assertions.assertFalse(result.isFraudulent);
        Assertions.assertFalse(result.isBlocked);
        Assertions.assertFalse(result.verificationRequired);
        Assertions.assertEquals(0, result.riskScore);
    }

//    @Test
//    void testCombinedFraudFactors() {
//        List<FraudDetectionSystem.Transaction> previousTransactions = new ArrayList<>();
//        LocalDateTime now = LocalDateTime.now();
//        for (int i = 0; i < 11; i++) {
//            previousTransactions.add(new FraudDetectionSystem.Transaction(100, now.minusMinutes(i * 5), "New York"));
//        }
//
//        FraudDetectionSystem.Transaction currentTransaction = new FraudDetectionSystem.Transaction(12000, now, "Los Angeles");
//        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, new ArrayList<>());
//
//        Assertions.assertTrue(result.isFraudulent);
//        Assertions.assertTrue(result.isBlocked);
//        Assertions.assertTrue(result.verificationRequired);
//        Assertions.assertEquals(100, result.riskScore);
//    }
    @Test
    void testExcessiveTransactionsInLastHour() {
        List<FraudDetectionSystem.Transaction> previousTransactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 11; i++) {
            previousTransactions.add(new FraudDetectionSystem.Transaction(100, now.minusMinutes(i * 5), "New York"));
        }

        FraudDetectionSystem.Transaction currentTransaction = new FraudDetectionSystem.Transaction(100, now, "New York");
        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, new ArrayList<>());

        Assertions.assertTrue(result.isBlocked);
        Assertions.assertTrue(result.riskScore >= 30);
    }

    @Test
    void testQuickLocationChangeWithinShortTimeFrame() {
        List<FraudDetectionSystem.Transaction> previousTransactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        previousTransactions.add(new FraudDetectionSystem.Transaction(100, now.minusMinutes(25), "New York"));

        FraudDetectionSystem.Transaction currentTransaction = new FraudDetectionSystem.Transaction(100, now, "Los Angeles");
        FraudDetectionSystem.FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, new ArrayList<>());

        Assertions.assertTrue(result.isFraudulent);
        Assertions.assertTrue(result.verificationRequired);
        Assertions.assertTrue(result.riskScore >= 20);
    }

}