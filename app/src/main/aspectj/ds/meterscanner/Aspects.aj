package ds.meterscanner;


import timber.log.Timber;

privileged aspect Logger {

    pointcut initViewModel(): execution(ds.meterscanner.databinding.BaseViewModel.new(..));

    //pointcut getDimensions() : execution(* Shape+.getDimensions());
    //pointcut getDimensionsNoSuper() : getDimensions() && !cflowbelow(getDimensions());

    after(): initViewModel() {
        Timber.d("aspectj: init %s", thisJoinPoint.getThis().getClass().getSimpleName());
        //Timber.d("aspect: %s", thisJoinPoint.toShortString());
    }

}