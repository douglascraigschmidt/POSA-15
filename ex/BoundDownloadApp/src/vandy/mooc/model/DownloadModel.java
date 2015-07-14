
    /**
     * The AIDL Interface that's used to make twoway calls to the
     * DownloadServiceSync Service.  This object plays the role of
     * Requestor in the Broker Pattern.  If it's null then there's no
     * connection to the Service.
     */
    DownloadCall mDownloadCall;
     
    /**
     * The AIDL Interface that we will use to make oneway calls to the
     * DownloadServiceAsync Service.  This plays the role of Requestor
     * in the Broker Pattern.  If it's null then there's no connection
     * to the Service.
     */
    DownloadRequest mDownloadRequest;
     
    /** 
     * This ServiceConnection is used to receive results after binding
     * to the DownloadServiceSync Service using bindService().
     */
    ServiceConnection mServiceConnectionSync = new ServiceConnection() {
            /**
             * Cast the returned IBinder object to the DownloadCall
             * AIDL Interface and store it for later use in
             * mDownloadCall.
             */
            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
            	Log.d(TAG, "ComponentName: " + name);
                // Call the generated stub method to convert the
                // service parameter into an interface that can be
                // used to make RPC calls to the Service.
                mDownloadCall =
                    DownloadCall.Stub.asInterface(service);
            }

            /**
             * Called if the remote service crashes and is no longer
             * available.  The ServiceConnection will remain bound,
             * but the service will not respond to any requests.
             */
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mDownloadCall = null;
            }
        };
     
    /** 
     * This ServiceConnection is used to receive results after binding
     * to the DownloadServiceAsync Service using bindService().
     */
    ServiceConnection mServiceConnectionAsync = new ServiceConnection() {
            /**
             * Cast the returned IBinder object to the DownloadRequest
             * AIDL Interface and store it for later use in
             * mDownloadRequest.
             */
            @Override
		public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                // Call the generated stub method to convert the
                // service parameter into an interface that can be
                // used to make RPC calls to the Service.
                mDownloadRequest =
                    DownloadRequest.Stub.asInterface(service);
            }

            /**
             * Called if the remote service crashes and is no longer
             * available.  The ServiceConnection will remain bound,
             * but the service will not respond to any requests.
             */
            @Override
		public void onServiceDisconnected(ComponentName name) {
                mDownloadRequest = null;
            }
        };
     
    /**
     * The implementation of the DownloadResults AIDL
     * Interface. Should be passed to the DownloadBoundServiceAsync
     * Service using the DownloadRequest.downloadImage() method.
     * 
     * This implementation of DownloadResults.Stub plays the role of
     * Invoker in the Broker Pattern.
     */
    DownloadResults.Stub mDownloadResults = new DownloadResults.Stub() {
            /**
             * Called when the DownloadServiceAsync finishes obtaining
             * the results from the GeoNames Web service.  Use the
             * provided String to display the results in a TextView.
             */
            @Override
            public void sendPath(final String imagePathname) throws RemoteException {
                // Create a new Runnable whose run() method displays
                // the bitmap image whose pathname is passed as a
                // parameter to sendPath().  Please use
                // displayBitmap() defined in DownloadBase.
                final Runnable displayRunnable = new Runnable() {
                        public void run() {
                            displayBitmap(imagePathname);
                        }
                    };

                runOnUiThread(displayRunnable);
            }
        };

         /**
     * Hook method called when the DownloadActivity becomes visible to
     * bind the Activity to the Services.
     */
    @Override
    public void onStart() {
    	super.onStart();
    	
    	// Bind this activity to the DownloadBoundService* Services if
    	// they aren't already bound Use mBoundSync/mBoundAsync
    	if(mDownloadCall == null) 
            bindService(DownloadBoundServiceSync.makeIntent(this), 
                        mServiceConnectionSync, 
                        BIND_AUTO_CREATE);
    	if(mDownloadRequest == null)
            bindService(DownloadBoundServiceAsync.makeIntent(this), 
                        mServiceConnectionAsync, 
                        BIND_AUTO_CREATE);
    }
    
    /**
     * Hook method called when the DownloadActivity becomes completely
     * hidden to unbind the Activity from the Services.
     */
    @Override
    public void onStop() {
    	super.onStop();
    	
    	// Unbind the Sync/Async Services if they are bound. Use
    	// mBoundSync/mBoundAsync
    	if(mDownloadCall != null) 
            unbindService(mServiceConnectionSync);
    	if(mDownloadRequest != null) 
            unbindService(mServiceConnectionAsync);
    }
    
    // Public accessor method for testing purposes
    public DownloadCall getDownloadCall() {
    	return mDownloadCall;
    }
    
    // Public accessor method for testing purposes
    public DownloadRequest getDownloadRequest() {
    	return mDownloadRequest;
    }
    
    // Public accessor method for testing purposes
    public DownloadResults getDownloadResults() {
    	return mDownloadResults;
    }
    
    // Public accessor method for testing purposes
    public boolean isBoundToSync() {
    	return mDownloadCall != null;
    }
    
    // Public accessor method for testing purposes
    public boolean isBoundToAsync() {
    	return mDownloadRequest != null;
    }     
}     
