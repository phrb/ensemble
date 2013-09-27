package mms.router;

import mms.Command;

/**
 *
 * @author lfthomaz
 */
public interface RouterClient {

    public String getAddress();

    public void processCommand(Command cmd);
    
    public void receiveCommand(Command cmd);

    public void sendCommand(Command cmd);
    

}
