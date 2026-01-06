# Flight Expiration Logic Fixes

## Issues Fixed

### 1. **DateCheck.java - Critical ConcurrentModificationException**
**Problem:** Iterating over `flights.values()` and removing items during iteration causes `ConcurrentModificationException`.
```java
// OLD - BROKEN
for (Flight flight : flights.values()) {
    if (flight.getDate().isBefore(date)) {
        flights.remove(flight.getFlightNum());  // ❌ Modifying map during iteration
    }
}
```

**Fix:** Create a separate list to collect flights to remove, then remove them in a second loop.
```java
// NEW - THREAD SAFE
List<Integer> flightsToRemove = new ArrayList<>();
for (Flight flight : flights.values()) {
    if (flight.getDate() != null && (flight.getDate().isBefore(date) || flight.getDate().isEqual(date))) {
        flightsToRemove.add(flight.getFlightNum());
    }
}
for (Integer flightNum : flightsToRemove) {
    flights.remove(flightNum);
}
```

### 2. **DateCheck.java - Incorrect Date Logic**
**Problem:** Only checking if flight date is `isBefore()` but not handling flights that expire today.
```java
// OLD - INCOMPLETE
if (flight.getDate().isBefore(date))  // Misses today's flights
```

**Fix:** Check both `isBefore()` AND `isEqual()` to include today's date.
```java
// NEW - CORRECT
if (flight.getDate() != null && (flight.getDate().isBefore(date) || flight.getDate().isEqual(date)))
```

### 3. **DateCheck.java - Missing Null Check**
**Problem:** No null check for `flight.getDate()` could cause NullPointerException.

**Fix:** Added null check: `if (flight.getDate() != null && ...)`

### 4. **DateCheck.java - Missing CSV Persistence**
**Problem:** Flights were removed from memory but never saved to CSV files.

**Fix:** Added `saveAndReloadData()` method that:
- Saves updated flights to `flights.csv` using `FileOp.saveFile()`
- Overwrites the entire file (not appending) for consistency
- Includes proper error handling and logging

### 5. **AirlineGUI.java - Non-Continuous Thread**
**Problem:** `run()` method only executed once instead of continuously checking for expired flights.
```java
// OLD - RUNS ONLY ONCE
public void run() {
    DateCheck.isDateInPast(LocalDate.now(), database.getFlights());
}
```

**Fix:** Implemented proper continuous background thread:
```java
// NEW - CONTINUOUS BACKGROUND THREAD
public void run() {
    System.out.println("[AirlineGUI Thread] Flight expiration checker started.");
    
    while (true) {  // ✅ Infinite loop for continuous checking
        try {
            Thread.sleep(30000);  // ✅ Check every 30 seconds
            
            synchronized (database) {  // ✅ Thread-safe access
                DateCheck.isDateInPast(LocalDate.now(), database.getFlights());
            }
            
        } catch (InterruptedException e) {
            System.out.println("[AirlineGUI Thread] Flight checker interrupted: " + e.getMessage());
            break;
        } catch (Exception e) {
            System.out.println("[AirlineGUI Thread] Error in flight expiration checker: " + e.getMessage());
            e.printStackTrace();
        }
    }
    System.out.println("[AirlineGUI Thread] Flight expiration checker stopped.");
}
```

### 6. **Thread Synchronization**
**Problem:** No synchronization when accessing the shared database from multiple threads.

**Fix:** Added `synchronized (database)` block to ensure thread-safe access:
```java
synchronized (database) {
    DateCheck.isDateInPast(LocalDate.now(), database.getFlights());
}
```

## How It Works Now

1. **Background Thread Initialization**: When AirlineGUI is instantiated as a Runnable and started in a thread, it continuously runs.

2. **Periodic Checks**: Every 30 seconds, the thread:
   - Wakes up and checks all flights in the database
   - Identifies flights with dates that have passed or are today
   - Thread-safely removes them from memory

3. **Data Persistence**: 
   - After deletion, the updated flights map is saved to `flights.csv`
   - File is overwritten to ensure consistency
   - Proper error handling if save fails

4. **Logging**: Console output shows:
   - When checker starts/stops
   - Which flights are being removed (with dates)
   - How many flights were removed
   - Any errors that occur

## Usage

Make sure to start the thread in your Main.java:
```java
Thread flightExpirationThread = new Thread(new AirlineGUI(database, true, currentUser));
flightExpirationThread.setDaemon(false);  // Optional: set to true if you want it to terminate with app
flightExpirationThread.start();
```

The thread will run indefinitely, checking for expired flights every 30 seconds and removing them automatically.

## Benefits

✅ **Thread-safe**: No ConcurrentModificationException  
✅ **Persistent**: Changes saved to CSV files  
✅ **Continuous**: Runs in background without user intervention  
✅ **Proper Logging**: Clear console output for debugging  
✅ **Robust Error Handling**: Catches and handles exceptions gracefully  
✅ **Configurable**: Easy to adjust check interval (change `Thread.sleep(30000)`)
