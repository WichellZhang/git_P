package zwatch.kerberos.ticket;

import com.sun.rowset.internal.Row;
import com.sun.xml.internal.bind.v2.model.core.ID;
import zwatch.kerberos.Utils;

public class Ticket_V {
    //Ticketv= E(KV, [Kc,v||IDC||ADC|| IDv||TS4||Lifetime4])

    public byte[] Kc_v;
    public String IDc, ADc, IDv;
    public long TS4, Lifetime4;

    public Ticket_V(byte[] Kc_v, String  IDc, String ADc, String IDv, long TS4, long Lifetime4){
        this.Kc_v=Kc_v;
        this.ADc=ADc;
        this.IDc=IDc;
        this.IDv=IDv;
        this.TS4=TS4;
        this.Lifetime4=Lifetime4;
    }
    public Ticket_V(byte[] Kc_v, String IDc, String ADc, String IDv, long TS4) {
        this(Kc_v, IDc, ADc, IDv, TS4, Utils.Default_Lifetime);
    }

    public String Pack(){
        return Utils.gson.toJson(this, Ticket_V.class);
    };

    public String CryptPack(byte[] KV) throws Exception {
        String ret=Pack();
        return Utils.encrypt_des(ret, KV);
    };

    public static Ticket_V UnPack(String RowData){
        return Utils.gson.fromJson(RowData, Ticket_V.class);
    };

    public static Ticket_V UnCryptPack(String RowData, byte[] pass) throws Exception {
        RowData=Utils.decrypt_des(RowData, pass);
        return UnPack(RowData);
    };
}
