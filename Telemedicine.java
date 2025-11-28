import java.util.concurrent.*;

// Blocks any transaction that exceeds safe limits.
class SafetyMonitor {
    private static final double MAX_SAFE_DOSE_MG = 500.0; // The safety limit

    public void validateTransaction(double dose) throws SecurityException {
        System.out.println("SafetyMonitor Checking dose: " + dose + "mg...");
        
        if (dose <= 0) {
             throw new SecurityException("CRITICAL ALERT: Invalid dose (Zero or Negative). Blocked.");
        }
        
        if (dose > MAX_SAFE_DOSE_MG) {
            throw new SecurityException("CRITICAL ALERT: Dosage " + dose + "mg exceeds safety limit of " + MAX_SAFE_DOSE_MG + "mg. Transaction BLOCKED.");
        }
        
        System.out.println("SafetyMonitor Check Passed. Dose is safe.");
    }
}


// Handles Unit Logic Kg, lbs, g
class DosageCalculator {
    private static final double RATE_PER_KG = 10.0; 

    public double calculateSafeDosage(double weightInput, String unit) {
        double weightInKg = 0.0;

        switch (unit.toLowerCase()) {
            case "kg":
                weightInKg = weightInput;
                break;
            case "lbs":
                System.out.println("SYstem Converting Lbs to Kg...");
                weightInKg = weightInput * 0.453592; 
                break;
            case "g":
            case "gram":
                System.out.println("system Converting Grams to Kg...");
                weightInKg = weightInput / 1000.0; 
                break;
            default:
                throw new IllegalArgumentException("Invalid unit. Accepted: kg, lbs, g");
        }

        if (weightInKg > 600.0) { 
             throw new IllegalArgumentException("Weight exceeds human limits (" + weightInKg + "kg). Check Input.");
        }

        System.out.println("system Standardized Weight: " + String.format("%.2f", weightInKg) + " kg");
        return weightInKg * RATE_PER_KG;
    }
}

// Simulates Network Reliability & Timeouts
class AllergyService {
    public String checkAllergiesWithTimeout(String patientId, int simulatedLatencyMs) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        Future<String> future = executor.submit(() -> {
            Thread.sleep(simulatedLatencyMs); 
            return "No Allergies Found";
        });

        try {
            System.out.println("Network Contacting Allergy Database (Limit: 2000ms)...");
            return future.get(2000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return "TIMEOUT"; 
        } catch (Exception e) {
            return "ERROR";
        } finally {
            executor.shutdown();
        }
    }
}

// MAIN SIMULATION --> The Workflow
public class Telemedicine {

    public static void main(String[] args) {
        SafetyMonitor monitor = new SafetyMonitor();
        DosageCalculator calculator = new DosageCalculator();
        AllergyService allergyService = new AllergyService();

        System.out.println("SAUDI TELEMEDICINE PLATFORM PROTOTYPE ");


        System.out.println("Scenario 1: Handling '20000g' Input ");
        try {
            double weight = 20000;
            String unit = "g";
            
            double dose = calculator.calculateSafeDosage(weight, unit);
            System.out.println("System Calculated Dose: " + dose + "mg");

            monitor.validateTransaction(dose);
            
            System.out.println("SUCCESS: Prescription sent to Pharmacy.\n");
        } catch (Exception e) {
            System.out.println("FAILURE: " + e.getMessage() + "\n");
        }

        // Doctor enters 80kg, but accidentally sets unit to kg, 
        System.out.println("Scenario 2: Protection System Activation ");
        try {
            double weight = 100; 
            String unit = "kg";
            
            double dose = calculator.calculateSafeDosage(weight, unit);
            System.out.println("System Calculated Dose: " + dose + "mg");

            monitor.validateTransaction(dose);
            
            System.out.println("SUCCESS: Prescription sent.\n");
        } catch (SecurityException e) {
            System.out.println("BLOCKED: " + e.getMessage());
            System.out.println("System prevented fatal error.\n");
        }

        System.out.println("Scenario 3: Fast Network Allergy Check ");
        String result = allergyService.checkAllergiesWithTimeout("Patient_123", 50);
        if (result.equals("TIMEOUT")) {
            System.out.println("ERROR: Network Timed out.");
        } else {
            System.out.println("SUCCESS: " + result + ". Proceeding with prescription.\n");
        }

        System.out.println("Scenario 4: Slow Network (3000ms lag)");
        result = allergyService.checkAllergiesWithTimeout("Patient_123", 3000);
        
        if (result.equals("TIMEOUT")) {
            System.out.println("ALERT: Network Timeout! Operation Aborted.");
            System.out.println("FAIL-SAFE: Doctor must verify allergies manually.");
        } else {
            System.out.println("SUCCESS: " + result);
        }
    }
}

