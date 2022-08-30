package rs.edu.raf.nwp.ispit.exception;

public class MachineAlreadyRunningException extends RuntimeException {
    public MachineAlreadyRunningException(){
        super("Machine already running");
    }
}
