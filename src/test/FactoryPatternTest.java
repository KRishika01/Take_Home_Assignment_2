package flight.reservation;

import flight.reservation.plane.Aircraft;
import flight.reservation.plane.AircraftFactory;
import flight.reservation.plane.Helicopter;
import flight.reservation.plane.PassengerDrone;
import flight.reservation.plane.PassengerPlane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Factory Pattern Tests")
public class FactoryPatternTest {

    private AircraftFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new AircraftFactory();
    }

    @Test
    @DisplayName("Should create all PassengerPlane models correctly")
    void testCreatePassengerPlaneModels() {
        String[] models = {"A380", "A350", "Embraer 190", "Antonov AN2"};
        int[] passengerCapacities = {500, 320, 25, 15};
        int[] crewCapacities = {42, 40, 5, 3};

        for (int i = 0; i < models.length; i++) {
            Aircraft aircraft = factory.createAircraft(models[i]);
            assertTrue(aircraft instanceof PassengerPlane);
            assertEquals(models[i], aircraft.getModel());
            assertEquals(passengerCapacities[i], aircraft.getPassengerCapacity());
            assertEquals(crewCapacities[i], aircraft.getCrewCapacity());
        }
    }

    @Test
    @DisplayName("Should create all Helicopter models correctly")
    void testCreateHelicopterModels() {
        String[] models = {"H1", "H2"};
        int[] capacities = {4, 6};

        for (int i = 0; i < models.length; i++) {
            Aircraft aircraft = factory.createAircraft(models[i]);
            assertTrue(aircraft instanceof Helicopter);
            assertEquals(models[i], aircraft.getModel());
            assertEquals(capacities[i], aircraft.getPassengerCapacity());
            assertEquals(2, aircraft.getCrewCapacity());
        }
    }

    @Test
    @DisplayName("Should create a PassengerDrone correctly")
    void testCreatePassengerDrone() {
        Aircraft aircraft = factory.createAircraft("HypaHype");
        assertTrue(aircraft instanceof PassengerDrone);
        assertEquals("HypaHype", aircraft.getModel());
        assertEquals(4, aircraft.getPassengerCapacity());
        assertEquals(0, aircraft.getCrewCapacity());
    }

    @Test
    @DisplayName("Should throw exception for unknown model")
    void testCreateInvalidModel() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createAircraft("NonExistentModel");
        });
    }
}
