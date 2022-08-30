package rs.edu.raf.nwp.ispit.exception;

public class MachineNotAvailableException extends RuntimeException{
    public MachineNotAvailableException(){
        super("Machine is currently not available");
    }
}
