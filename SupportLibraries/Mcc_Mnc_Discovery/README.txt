Supported Android Versions -->
Android 5.1 and above


Permissions needed in order to use the class -->
 android.permission.READ_PHONE_STATE
 
 
isOperatorEligible() method of  Mcc_mnv_Discovery-->
>  Initialize Mcc_mnv_Discovery object by Passing Activity instance 
>  Invoke this method from a mobile with single sim . It will Toast Mcc , Mnc and operator name 
>  Invoke this method from a mobile with dual sim . It will Toast Mcc , Mnc and operator name of sim slot 1 
>  It will Toast false if operator is not eligible 
>  Dont forget to grant Telephone permission to the app that has to be run on Android version 6 or above


isOperatorEligible(int SimSlot) method of  Mcc_mnv_Discovery-->
>  Initialize Mcc_mnv_Discovery object by Passing Activity instance 
>  Invoke this method with int parameter whose value will be sim slot number
>  It will Toast Mcc , Mnc and operator name of sim  whose sim slot number is passed 
>  It will Toast false if operator is not eligible
>  It will Toast false if sim slot number is not valid
>  Dont forget to grant Telephone permission to the app that has to be run on Android version 6 or above

