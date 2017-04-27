package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Kalos on 05/04/2017.
 */

public class Gestore implements Serializable {

    public Soldo[] Calculate_Credits(Persona pagante, Policy policy, Double importo, HashMap<String,Persona> persone, int n_persone, Persona user, Gruppo g) {
        Soldo[] crediti = new Soldo[n_persone];
        Double[] percentages;
        //ogni casella coincide con 1 partecipante
        percentages=policy.getPercentage();
        Soldo c;
        int i=0;

        //suppongo che ci sia corrispondenza 1:1 prima casella di ogni cosa rappresenta il primo username nell'hashmap e così via;
        for(Persona p: persone.values()){
            if(p.getUserName().equals(pagante.getUserName())){
                Double parte= importo*(percentages[p.getPosizione(g)]/100);
                c= new Soldo(p, parte,true,pagante);
                crediti[p.getPosizione(g)]=c;

            }
            else {
                p.setHaDebiti(true);
                p.setDove_Ho_debito(g,new Integer(1));
                Double parte= importo*(percentages[p.getPosizione(g)]/100);
                c= new Soldo(p, parte,false,pagante);
                crediti[p.getPosizione(g)]=c;

            }
        }

        return crediti;
    }

    public Soldo[] Calculate_Credits_To_Buyer_With_Repaing(Persona pagante, Policy policy, Double importo, HashMap<String,Persona> persone, int n_persone, HashMap<String,Spesa>spese, Persona user, Gruppo g) {
        //sto pagando la nuova spesa per me e per uno con cui ho il debito

        Soldo[] crediti = new Soldo[n_persone];
        Double[] percentages;
        //ogni casella coincide con 1 partecipante
        percentages=policy.getPercentage();
        Soldo c;
        int i=0;

        //DecimalFormat df = new DecimalFormat("#.###");
        //df.setRoundingMode(RoundingMode.CEILING); //da capire bene, ritornano stringhe

        //suppongo che ci sia corrispondenza 1:1 prima casella di ogni cosa rappresenta il primo username nell'hashmap e così via;

        //calcolare le posizioni in cui devo fare gli scambi di credito e debito


        HashMap<String,Soldo> debiti_da_ripagare= new HashMap<String,Soldo>();

        if(user.getHaDebiti()){

            for(Persona p: persone.values()){

                if(p.getUserName().equals(pagante.getUserName())){
                    Double parte= importo*(percentages[p.getPosizione(g)]/100);
                    c= new Soldo(p, parte,true,pagante);
                    crediti[p.getPosizione(g)]=c;
                }
                else {
                    Double parte= importo*(percentages[p.getPosizione(g)]/100);
                    c= new Soldo(p, parte,false,pagante);
                    crediti[p.getPosizione(g)]=c;
                    p.setDove_Ho_debito(g,new Integer(1));
                }
            } //calcolo tutte le parti che devono le persone secondo la policy stabilita per la spesa.



            for(Spesa s:spese.values()){ //ora voglio ripagare quelli con cui ho debiti -> vado a fare la ricerca di tutte le spese passate dove non ho pagato io
                boolean done=false;
                if(!user.getUserName().equals(s.getPagante().getUserName())){  //se non ho pagato io

                    Persona creditore  = s.getPagante(); //ora so a chi devo dei soldi
                    debiti_da_ripagare.putAll(s.getDivisioni()); //mi prendo le divisioni per quella spesa
                    Soldo importo_da_ridare = debiti_da_ripagare.get(user.getUserName()); //vedo quanti soldi gli devo
                    if(!importo_da_ridare.getHaPagato()){//-> ora ho di una spesa in cui io sono in debito //vedo se li ho dati o meno
                        //caso 1 il mio debito vecchio è maggiore di quanto lui deve mettere per questa spesa 8€> 6€ -> il suo debito nei miei confronti scende a zero e lui ha pagato
                        if(importo_da_ridare.getImporto() > crediti[creditore.getPosizione(g)].getImporto() && !done){ // il mio debito è maggiore di quanto posso pagare per lui
                            importo_da_ridare.sottraiImporto(crediti[creditore.getPosizione(g)].getImporto()); //tolgo dal mio debito la sua parte
                            crediti[user.getPosizione(g)].aggiungiImporto(crediti[creditore.getPosizione(g)].getImporto()); //aggiungo alla mia parte la sua parte
                            crediti[creditore.getPosizione(g)].sottraiImporto(crediti[creditore.getPosizione(g)].getImporto());
                            creditore.setDove_Ho_debito(g,creditore.CheckIfHasDebts(g));
                            crediti[creditore.getPosizione(g)].setHaPagato(true); //il suo debito nei miei confronti è sceso a zero
                            done=true;
                        }
                        //Caso 2 il mio debito è minore di quanto lui deve pagare 8€< 12€ il mio debito scende a zero e lui NON ha pagato
                        else if(importo_da_ridare.getImporto() < crediti[creditore.getPosizione(g)].getImporto() && !done){ // il mio debito è maggiore di quanto posso pagare per lui

                            Double f=importo_da_ridare.getImporto();

                            importo_da_ridare.sottraiImporto(f); //ho tolto tutto il mio debito
                            crediti[user.getPosizione(g)].aggiungiImporto(f); //ho aggiunto tutto il mio debito alla nuova spesa
                            crediti[creditore.getPosizione(g)].sottraiImporto(f);
                            s.getDivisioni().get(user.getUserName()).setHaPagato(true);
                            user.setDove_Ho_debito(g,user.CheckIfHasDebts(g));
                            done=true;
                        }
                        //Caso 3 il mio debito è uguale alla sua parte e sia il mio debito sia la sua parte sono state pagate!
                        else if(importo_da_ridare.getImporto() == crediti[creditore.getPosizione(g)].getImporto() && !done){ // il mio debito è maggiore di quanto posso pagare per lui

                            Double f=importo_da_ridare.getImporto();

                            importo_da_ridare.sottraiImporto(f); //ho tolto tutto il mio debito
                            crediti[user.getPosizione(g)].aggiungiImporto(f); //ho aggiunto tutto il mio debito alla nuova spesa
                            crediti[creditore.getPosizione(g)].sottraiImporto(f);
                            s.getDivisioni().get(user.getUserName()).setHaPagato(true);
                            crediti[creditore.getPosizione(g)].setHaPagato(true);
                            user.setDove_Ho_debito(g,user.CheckIfHasDebts(g));
                            creditore.setDove_Ho_debito(g,creditore.CheckIfHasDebts(g));
                            done=true;
                        }


                    }
                }

            }



            return crediti;
        }
        return null;
    }




}
