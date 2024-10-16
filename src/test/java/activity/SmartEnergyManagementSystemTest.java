package activity;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.Assert.*;

public class SmartEnergyManagementSystemTest {
    private SmartEnergyManagementSystem energySystem;

    @BeforeEach
    void setUp() {
        energySystem = new SmartEnergyManagementSystem();
    }

    @Test
    void testEnergySavingMode() {
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("TV", 2);
        devicePriorities.put("Fridge", 1);

        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
                0.5, 0.4, devicePriorities, LocalDateTime.now(), 22.0,
                new double[]{20.0, 25.0}, 100.0, 50.0, new ArrayList<>());

        Assertions.assertTrue(result.energySavingMode);
        Assertions.assertFalse(result.deviceStatus.get("TV"));
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

//    @Test
//    void testTemperatureRegulation() {
//        Map<String, Integer> devicePriorities = new HashMap<>();
//        devicePriorities.put("Heating", 1);
//        devicePriorities.put("Cooling", 1);
//
//        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
//                0.3, 0.4, devicePriorities, LocalDateTime.now(), 18.0,
//                new double[]{20.0, 25.0}, 100.0, 50.0, new ArrayList<>());
//
//        Assertions.assertTrue(result.temperatureRegulationActive);
//        Assertions.assertTrue(result.deviceStatus.get("Heating"));
//        Assertions.assertFalse(result.deviceStatus.get("Cooling"));
//    }

//    @Test
//    void testEnergyLimitApproached() {
//        Map<String, Integer> devicePriorities = new HashMap<>();
//        devicePriorities.put("TV", 2);
//        devicePriorities.put("Fridge", 1);
//
//        SmartEnergyManagementSystem.EnergyManagementResult result = energySystem.manageEnergy(
//                0.3, 0.4, devicePriorities, LocalDateTime.now(), 22.0,
//                new double[]{20.0, 25.0}, 90.0, 89.0, new ArrayList<>());
//
//        Assertions.assertFalse(result.deviceStatus.get("TV"));
//        Assertions.assertTrue(result.deviceStatus.get("Fridge"));
//    }

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
}
