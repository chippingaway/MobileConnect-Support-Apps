import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionManager;
import android.widget.Toast;

public class Mcc_Mnc_Discovery {

 private static Context context;

    public Mcc_Mnc_Discovery( Context con) {
        context = con;
    }

    public void isOperatorEligible()
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subManager = (SubscriptionManager) context.getSystemService(context.TELEPHONY_SUBSCRIPTION_SERVICE);
         String dn = ((String) subManager.getActiveSubscriptionInfoForSimSlotIndex(0).getDisplayName()).toLowerCase();
         String mcc = String.valueOf(subManager.getActiveSubscriptionInfoForSimSlotIndex(0).getMcc());
            String mnc = String.valueOf(subManager.getActiveSubscriptionInfoForSimSlotIndex(0).getMnc());
            if(dn.contains("voda") || dn.contains("docomo") || dn.contains("airtel") || dn.contains("aircel") || dn.contains("dea") || dn.contains("telenor") )
            {
                Toast.makeText(context,mcc + " "+mnc,Toast.LENGTH_SHORT).show();
                Toast.makeText(context,dn,Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context,"false",Toast.LENGTH_SHORT).show();

        }
    }


    public void isOperatorEligible(int sim)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                  try {
                      SubscriptionManager subManager = (SubscriptionManager) context.getSystemService(context.TELEPHONY_SUBSCRIPTION_SERVICE);
                      String dn = ((String) subManager.getActiveSubscriptionInfoForSimSlotIndex(sim - 1).getDisplayName()).toLowerCase();
                      String mcc = String.valueOf(subManager.getActiveSubscriptionInfoForSimSlotIndex(sim - 1).getMcc());
                      String mnc = String.valueOf(subManager.getActiveSubscriptionInfoForSimSlotIndex(sim - 1).getMnc());
                      if (dn.contains("voda") || dn.contains("docomo") || dn.contains("airtel") || dn.contains("aircel") || dn.contains("dea") || dn.contains("telenor")) {
                          Toast.makeText(context, mcc + " " + mnc, Toast.LENGTH_SHORT).show();
                          Toast.makeText(context, dn, Toast.LENGTH_SHORT).show();
                      } else
                          Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
                  }catch (NullPointerException e){Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();}
                  catch (Exception e){Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();}

              }
    }


}
