package utenti.diario.utilities.internet;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by SosiForWork on 01/08/2017.
 */

public class InternetManager extends AsyncTask<InternetCheck,Void,Void> {

        @Override
        protected Void doInBackground(InternetCheck... params) {

            InternetCheck cl=params[0];

            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();
                cl.OnConnectionChecked(true);

            } catch(IOException e) {
                cl.OnConnectionChecked(false);

            }

            return null;
        }


}
