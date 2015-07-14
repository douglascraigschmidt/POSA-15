            if (mDownloadCall != null) {
                Log.d(TAG,
                      "Calling twoway DownloadServiceSync.downloadImage()");
                /** 
                 * Define an AsyncTask instance to avoid blocking the UI Thread. 
                 * */
		new AsyncTask<Uri, Void, String>() {
                    /**
                     * Runs in a background thread.
                     */
                    @Override
                    protected String doInBackground(Uri... params) {
                        try {
                            return mDownloadCall.downloadImage(params[0]);
                        } catch(RemoteException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    /**
                     * Runs in the UI Thread.
                     */
                    @Override
                    protected void onPostExecute(String result) {
                        if (result != null) 
                            displayBitmap(result);
                    }
                }.execute(uri);
            }

            if(mDownloadRequest != null) {
                try {
                    Log.d(TAG,
                          "Calling oneway DownloadServiceAsync.downloadImage()");

                    // Call downloadImage() on mDownloadRequest, passing in
                    // the appropriate Uri and Results.
                    mDownloadRequest.downloadImage(uri,
                                                   mDownloadResults);
                } catch(RemoteException e) {
                    e.printStackTrace();
                }
            }
