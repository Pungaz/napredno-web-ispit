package rs.edu.raf.nwp.ispit.exception;

public class MachineAlreadyStoppedException extends RuntimeException {
    public MachineAlreadyStoppedException(){
        super("Machine already stopped");
    }
}
