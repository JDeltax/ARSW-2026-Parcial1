package edu.eci.arsw.blacklistvalidator;
import edu.eci.arsw.spamkeywordsdatasource.*;
import java.util.*;
public class BlackListThread extends Thread{
    private int start;
    private int end;
    private String ipAddress;
    public HostBlacklistsDataSourceFacade sdks;
    private int ocurrencesCount = 0;
    private List<Integer> blackListOcurrences = new LinkedList<>();

    public BlackListThread(int start, int end, String ipAddress, HostBlacklistsDataSourceFacade sdks){
        this.start = start;
        this.end = end;
        this.ipAddress = ipAddress;
        this.sdks = sdks;

    }

    @Override 
    public void run(){

        for (int i = start; i < end && ocurrencesCount < 5; i++){
            if(sdks.isInBlackListServer(i, ipAddress)){
                ocurrencesCount++;
                blackListOcurrences.add(i);
            }
        }
    }


    //getter sociooo
    public int getOcurrencesCount(){
        return ocurrencesCount;
    }

    public List<Integer> getBlackListOcurrences(){
        return blackListOcurrences;
    }
}