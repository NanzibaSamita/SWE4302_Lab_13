import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Flight extends FlightDistance {

    //        ************************************************************ Fields ************************************************************

    private final String flightSchedule;
    private final String flightNumber;
    private final String fromWhichCity;
    private final String gate;
    private final String toWhichCity;
    private double distanceInMiles;
    private double distanceInKm;
    private String flightTime;
    private int numOfSeatsInTheFlight;
    private List<Customer> listOfRegisteredCustomersInAFlight;
    private int customerIndex;
    private static int nextFlightDay = 0;
    private static final List<Flight> flightList = new ArrayList<>();

    private FlightStatus status = FlightStatus.SCHEDULED;

    // Add getter and setter
    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    //        ************************************************************ Behaviours/Methods ************************************************************

    Flight() {
        this.flightSchedule = null;
        this.flightNumber = null;
        this.numOfSeatsInTheFlight = 0;
        toWhichCity = null;
        fromWhichCity = null;
        this.gate = null;
    }

    /**
     * Creates new random flight from the specified arguments.
     *
     * @param flightSchedule           includes departure date and time of flight
     * @param flightNumber             unique identifier of each flight
     * @param numOfSeatsInTheFlight    available seats in the flight
     * @param chosenDestinations       consists of origin and destination airports(cities)
     * @param distanceBetweenTheCities gives the distance between the airports both in miles and kilometers
     * @param gate                     from where passengers will board to the aircraft
     */
    Flight(String flightSchedule, String flightNumber, int numOfSeatsInTheFlight, String[][] chosenDestinations, String[] distanceBetweenTheCities, String gate) {
        this.flightSchedule = flightSchedule;
        this.flightNumber = flightNumber;
        this.numOfSeatsInTheFlight = numOfSeatsInTheFlight;
        this.fromWhichCity = chosenDestinations[0][0];
        this.toWhichCity = chosenDestinations[1][0];
        this.distanceInMiles = Double.parseDouble(distanceBetweenTheCities[0]);
        this.distanceInKm = Double.parseDouble(distanceBetweenTheCities[1]);
        this.flightTime = calculateFlightTime(distanceInMiles);
        this.listOfRegisteredCustomersInAFlight = new ArrayList<>();
        this.gate = gate;
    }

    /**
     * Creates Flight Schedule. All methods of this class are collaborating with each other
     * to create flight schedule of the said length in this method.
     */
    public void flightScheduler() {
        int numOfFlights = 15;              // decides how many unique flights to be included/display in scheduler
        RandomGenerator r1 = new RandomGenerator();
        for (int i = 0; i < numOfFlights; i++) {
            String[][] chosenDestinations = r1.randomDestinations();
            String[] distanceBetweenTheCities = calculateDistance(Double.parseDouble(chosenDestinations[0][1]), Double.parseDouble(chosenDestinations[0][2]), Double.parseDouble(chosenDestinations[1][1]), Double.parseDouble(chosenDestinations[1][2]));
            String flightSchedule = createNewFlightsAndTime();
            String flightNumber = r1.randomFlightNumbGen(2, 1).toUpperCase();
            int numOfSeatsInTheFlight = r1.randomNumOfSeats();
            String gate = r1.randomFlightNumbGen(1, 30);
            flightList.add(new Flight(flightSchedule, flightNumber, numOfSeatsInTheFlight, chosenDestinations, distanceBetweenTheCities, gate.toUpperCase()));
        }
    }

    /**
     * Registers new Customer in this Flight.
     *
     * @param customer customer to be registered
     */
    void addNewCustomerToFlight(Customer customer) {
        this.listOfRegisteredCustomersInAFlight.add(customer);
    }

    /**
     * Adds numOfTickets to existing customer's tickets for the this flight.
     *
     * @param customer     customer in which tickets are to be added
     * @param numOfTickets number of tickets to add
     */

    void addTicketsToExistingCustomer(Customer customer, int numOfTickets) {
        // First find the customer's index in the flight's customer list
        int index = listOfRegisteredCustomersInAFlight.indexOf(customer);
        if (index >= 0) {
            // Get the customer from the flight's list
            Customer existingCustomer = listOfRegisteredCustomersInAFlight.get(index);
            // Add tickets to this customer's booking for this flight
            existingCustomer.addTicketsToFlight(this, numOfTickets);
        }
    }

    /***
     * Checks if the specified customer is already registered in the FLight's array list
     * @param customersList of the flight
     * @param customer specified customer to be checked
     * @return true if the customer is already registered in the said flight, false otherwise
     */
    boolean isCustomerAlreadyAdded(List<Customer> customersList, Customer customer) {
        return listOfRegisteredCustomersInAFlight.stream()
                .anyMatch(c -> c.getUserID().equals(customer.getUserID()));
    }

    /**
     * Calculates the flight time, using avg. ground speed of 450 knots.
     *
     * @param distanceBetweenTheCities distance between the cities/airports in miles
     * @return formatted flight time
     */


    public String calculateFlightTime(double distanceBetweenTheCities) {
        double[] hoursAndMinutes = calculateRawFlightTime(distanceBetweenTheCities);
        return formatFlightTime(hoursAndMinutes[0], hoursAndMinutes[1]);
    }

    private double[] calculateRawFlightTime(double distance) {
        double groundSpeed = 450;
        double time = (distance / groundSpeed);
        String timeInString = String.format("%.4s", time);
        String[] timeArray = timeInString.replace('.', ':').split(":");
        return new double[] {
                Double.parseDouble(timeArray[0]),
                Double.parseDouble(timeArray[1])
        };
    }

    private String formatFlightTime(double hours, double minutes) {
        // Adjust minutes to nearest 5-minute interval
        int modulus = (int)minutes % 5;
        if (modulus < 3) {
            minutes -= modulus;
        } else {
            minutes += 5 - modulus;
        }

        // Handle overflow (when minutes >= 60)
        if (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        // Format based on hours and minutes length
        boolean singleDigitHour = hours <= 9;
        boolean singleDigitMinute = String.valueOf((int)minutes).length() == 1;

        if (singleDigitHour && singleDigitMinute) {
            return String.format("0%d:0%d", (int)hours, (int)minutes);
        } else if (singleDigitHour) {
            return String.format("0%d:%d", (int)hours, (int)minutes);
        } else if (singleDigitMinute) {
            return String.format("%d:0%d", (int)hours, (int)minutes);
        } else {
            return String.format("%d:%d", (int)hours, (int)minutes);
        }
    }


    /**
     * Creates flight arrival time by adding flight time to flight departure time
     *
     * @return flight arrival time
     */
    public String fetchArrivalTime() {
        /*These lines convert the String of flightSchedule to LocalDateTIme and add the arrivalTime to it....*/
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm a ");
        LocalDateTime departureDateTime = LocalDateTime.parse(flightSchedule, formatter);

        /*Getting the Flight Time, plane was in air*/
        String[] flightTime = getFlightTime().split(":");
        int hours = Integer.parseInt(flightTime[0]);
        int minutes = Integer.parseInt(flightTime[1]);


        LocalDateTime arrivalTime;

        arrivalTime = departureDateTime.plusHours(hours).plusMinutes(minutes);
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EE, dd-MM-yyyy HH:mm a");
        return arrivalTime.format(formatter1);

    }

    void deleteFlight(String flightNumber) {
        boolean isFound = false;
        Iterator<Flight> list = flightList.iterator();
        while (list.hasNext()) {
            Flight flight = list.next();
            if (flight.getFlightNumber().equalsIgnoreCase(flightNumber)) {
                isFound = true;
                break;
            }
        }
        if (isFound) {
            list.remove();
        } else {
            System.out.println("Flight with given Number not found...");
        }
        displayFlightSchedule();
    }

    /**
     * Calculates the distance between the cities/airports based on their lat longs.
     *
     * @param lat1 origin city/airport latitude
     * @param lon1 origin city/airport longitude
     * @param lat2 destination city/airport latitude
     * @param lon2 destination city/airport longitude
     * @return distance both in miles and km between the cities/airports
     */
    @Override
    public String[] calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double distance = Math.sin(degreeToRadian(lat1)) * Math.sin(degreeToRadian(lat2)) + Math.cos(degreeToRadian(lat1)) * Math.cos(degreeToRadian(lat2)) * Math.cos(degreeToRadian(theta));
        distance = Math.acos(distance);
        distance = radianToDegree(distance);
        distance = distance * 60 * 1.1515;
        /* On the Zero-Index, distance will be in Miles, on 1st-index, distance will be in KM and on the 2nd index distance will be in KNOTS*/
        String[] distanceString = new String[3];
        distanceString[0] = String.format("%.2f", distance * 0.8684);
        distanceString[1] = String.format("%.2f", distance * 1.609344);
        distanceString[2] = Double.toString(Math.round(distance * 100.0) / 100.0);
        return distanceString;
    }

    private double degreeToRadian(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double radianToDegree(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void displayFlightSchedule() {

        Iterator<Flight> flightIterator = flightList.iterator();
        System.out.println();
        System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+------------------------+\n");
        System.out.printf("| Num  | FLIGHT SCHEDULE\t\t\t   | FLIGHT NO | Available Seats  | \tFROM ====>>       | \t====>> TO\t   | \t    ARRIVAL TIME       | FLIGHT TIME |  GATE  |   DISTANCE(MILES/KMS)  |%n");
        System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+------------------------+\n");
        int i = 0;
        while (flightIterator.hasNext()) {
            i++;
            Flight f1 = flightIterator.next();
            System.out.println(f1.toString(i));
            System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+------------------------+\n");
        }
    }

    @Override
    public String toString(int i) {
        return String.format("| %-5d| %-41s | %-9s | \t%-9s | %-21s | %-22s | %-10s  |   %-6sHrs |  %-4s  |  %-8s / %-11s|", i, flightSchedule, flightNumber, numOfSeatsInTheFlight, fromWhichCity, toWhichCity, fetchArrivalTime(), flightTime, gate, distanceInMiles, distanceInKm);
    }

    /**
     * Creates new random flight schedule
     *
     * @return newly created flight schedule
     */
    public String createNewFlightsAndTime() {

        Calendar c = Calendar.getInstance();
        // Incrementing nextFlightDay, so that next scheduled flight would be in the future, not in the present
        nextFlightDay += Math.random() * 7;
        c.add(Calendar.DATE, nextFlightDay);
        c.add(Calendar.HOUR, nextFlightDay);
        c.set(Calendar.MINUTE, ((c.get(Calendar.MINUTE) * 3) - (int) (Math.random() * 45)));
        Date myDateObj = c.getTime();
        LocalDateTime date = Instant.ofEpochMilli(myDateObj.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        date = getNearestHourQuarter(date);
        return date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm a "));
    }

    /**
     * Formats flight schedule, so that the minutes would be to the nearest quarter.
     *
     * @param datetime to be formatting
     * @return formatted LocalDateTime with minutes close to the nearest hour quarter
     */
    public LocalDateTime getNearestHourQuarter(LocalDateTime datetime) {
        int minutes = datetime.getMinute();
        int mod = minutes % 15;
        LocalDateTime newDatetime;
        if (mod < 8) {
            newDatetime = datetime.minusMinutes(mod);
        } else {
            newDatetime = datetime.plusMinutes(15 - mod);
        }
        newDatetime = newDatetime.truncatedTo(ChronoUnit.MINUTES);
        return newDatetime;
    }


    //        ************************************************************ Setters & Getters ************************************************************

    public int getNoOfSeats() {
        return numOfSeatsInTheFlight;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setNoOfSeatsInTheFlight(int numOfSeatsInTheFlight) {
        this.numOfSeatsInTheFlight = numOfSeatsInTheFlight;
    }

    public String getFlightTime() {
        return flightTime;
    }

    public List<Flight> getFlightList() {
        return flightList;
    }

    public List<Customer> getListOfRegisteredCustomersInAFlight() {
        return listOfRegisteredCustomersInAFlight;
    }

    public String getFlightSchedule() {
        return flightSchedule;
    }

    public String getFromWhichCity() {
        return fromWhichCity;
    }

    public String getGate() {
        return gate;
    }

    public String getToWhichCity() {
        return toWhichCity;
    }

}