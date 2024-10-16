package activity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

public class SmartEnergyManagementSystemTest {
    private SmartEnergyManagementSystem energySystem;

    @BeforeEach
    void setUp() {
        energySystem = new SmartEnergyManagementSystem();
    }

    @Test
    void testTemperatureRegulation() {
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Heating", 1);
        devicePriorities.put("Cooling", 1);

        // Test for temperature below the desired range (Heating should turn on)
        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, LocalDateTime.now(), 18.0,
                new double[]{20.0, 24.0}, 100.0, 50.0, new ArrayList<>());

        Assertions.assertTrue(result.temperatureRegulationActive);
        Assertions.assertTrue(result.deviceStatus.get("Heating"));
        Assertions.assertFalse(result.deviceStatus.get("Cooling"));

        // Test for temperature above the desired range (Cooling should turn on)
        result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, LocalDateTime.now(), 26.0,
                new double[]{20.0, 24.0}, 100.0, 50.0, new ArrayList<>());

        Assertions.assertTrue(result.temperatureRegulationActive);
        Assertions.assertFalse(result.deviceStatus.get("Heating"));
        Assertions.assertTrue(result.deviceStatus.get("Cooling"));

        // Test for temperature within the desired range (Neither Heating nor Cooling should be active)
        result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, LocalDateTime.now(), 22.0,
                new double[]{20.0, 24.0}, 100.0, 50.0, new ArrayList<>());

        // Since 22.0 is within the range [20.0, 24.0], temperature regulation should not be active.
        Assertions.assertFalse(result.temperatureRegulationActive);
        Assertions.assertFalse(result.deviceStatus.get("Heating"));
        Assertions.assertFalse(result.deviceStatus.get("Cooling"));
    }

    @Test
    void testEnergyLimitApproached() {
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("TV", 2);
        devicePriorities.put("Fridge", 1);

        // Test with totalEnergyUsedToday approaching the energy usage limit
        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, LocalDateTime.now(), 22.0,
                new double[]{20.0, 25.0}, 90.0, 90.0, new ArrayList<>());

        Assertions.assertFalse(result.deviceStatus.get("TV")); // Low-priority device should turn off
        Assertions.assertTrue(result.deviceStatus.get("Fridge")); // High-priority device remains on

        // Test when energy usage is not near the limit, all devices should remain on
        result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, LocalDateTime.now(), 22.0,
                new double[]{20.0, 25.0}, 100.0, 50.0, new ArrayList<>());

        Assertions.assertTrue(result.deviceStatus.get("TV"));
        Assertions.assertTrue(result.deviceStatus.get("Fridge"));
    }

    @Test
    void testNoPriceThresholdBreach() {
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("TV", 2);
        devicePriorities.put("Fridge", 1);

        // Test with current price below the threshold
        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, LocalDateTime.now(), 22.0,
                new double[]{20.0, 25.0}, 100.0, 50.0, new ArrayList<>());

        Assertions.assertFalse(result.energySavingMode);
        Assertions.assertTrue(result.deviceStatus.get("TV"));
        Assertions.assertTrue(result.deviceStatus.get("Fridge"));
    }

    @Test
    void testNightMode() {
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("TV", 1);
        devicePriorities.put("Security", 1);
        devicePriorities.put("Refrigerator", 1);

        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, LocalDateTime.of(2023, 1, 1, 23, 30),
                22.0, new double[]{20.0, 25.0}, 100.0, 50.0, new ArrayList<>());

        Assertions.assertFalse(result.deviceStatus.get("TV"));
        Assertions.assertTrue(result.deviceStatus.get("Security"));
        Assertions.assertTrue(result.deviceStatus.get("Refrigerator"));
    }

    @Test
    void testScheduledDevices() {
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("WashingMachine", 2);

        LocalDateTime now = LocalDateTime.now();
        List<SmartEnergyManagementSystem.DeviceSchedule> scheduledDevices = new ArrayList<>();
        scheduledDevices.add(new SmartEnergyManagementSystem.DeviceSchedule("WashingMachine", now));

        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
                0.3, 0.4, devicePriorities, now, 22.0,
                new double[]{20.0, 25.0}, 100.0, 50.0, scheduledDevices);

        Assertions.assertTrue(result.deviceStatus.get("WashingMachine"));
    }

    @Test
    void testEnergySavingMode() {
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Heating", 1);  // High priority
        devicePriorities.put("Lights", 2);   // Low priority
        devicePriorities.put("Appliances", 3);  // Low priority

        // Test with current price above the threshold
        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
                0.25, 0.20, devicePriorities, LocalDateTime.now(), 22.0,
                new double[]{20.0, 25.0}, 100.0, 50.0, new ArrayList<>());

        // Check if energy-saving mode is activated
        Assertions.assertTrue(result.energySavingMode);

        // Check if low-priority devices are turned off
        Assertions.assertFalse(result.deviceStatus.get("Lights"));
        Assertions.assertFalse(result.deviceStatus.get("Appliances"));

        // Check if high-priority devices remain on
        Assertions.assertTrue(result.deviceStatus.get("Heating"));

        // Test with current price below the threshold
        result = energySystem.manageEnergy(
                0.15, 0.20, devicePriorities, LocalDateTime.now(), 22.0,
                new double[]{20.0, 25.0}, 100.0, 50.0, new ArrayList<>());

        // Check if energy-saving mode is not activated
        Assertions.assertFalse(result.energySavingMode);

        // Check if all devices are on when not in energy-saving mode
        Assertions.assertTrue(result.deviceStatus.get("Heating"));
        Assertions.assertTrue(result.deviceStatus.get("Lights"));
        Assertions.assertTrue(result.deviceStatus.get("Appliances"));
    }
}
