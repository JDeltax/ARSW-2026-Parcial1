/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.eci.arsw.blacklistvalidator.BlackListThread;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int n){ // solo añadí el int n soicitado por el profe para la de los hilos
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        int ocurrencesCount=0;
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int totalServers = skds.getRegisteredServersCount();
        int base = totalServers / n;
        int resto = totalServers%n;
        int inicio = 0;
        BlackListThread[] threads = new BlackListThread[n];


        for(int i = 0 ; i < n ; i++){
            int tamano = base + (i == n -1 ? resto : 0);
            int finalito = inicio + tamano - 1;
            threads[i] = new BlackListThread(inicio,finalito, ipaddress , skds);
            threads[i].start();
        }

        for (BlackListThread t : threads){
            try{
                t.join();
            }catch(InterruptedException log){
                

            }
        }

        for (BlackListThread t : threads){
            ocurrencesCount += t.getOcurrencesCount();
            blackListOcurrences.addAll(t.getBlackListOcurrences());
        }


        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
