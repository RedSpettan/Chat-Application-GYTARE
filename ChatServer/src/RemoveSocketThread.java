public class RemoveSocketThread implements Runnable {

    private Server activeServer;

    public RemoveSocketThread(Server server) {

        this.activeServer = server;

    }

    @Override
    public void run() {

        System.out.println("Thread has started!");

        while (true) {
            //System.out.println(activeServer.socketList.isEmpty());

            if (activeServer.socketList.size() > 0) {
                //System.out.println("Hello!");
                for (int x = 0; x < activeServer.socketList.size(); x++) {
                    System.out.println("Socket " + x + " is closed: " + activeServer.socketList.get(x).isClosed());
                    if (activeServer.socketList.get(x).isClosed()) {

                        System.out.println(activeServer.socketList.get(x) + " Socket is no longer available!");

                        //System.out.println(socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                        activeServer.socketThreadMap.remove(activeServer.socketList.get(x));

                        //System.out.println("Is Alive?: " + socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                        activeServer.socketList.remove(x);

                        System.out.println("Socket has been removed!");

                        //System.exit(0);

                        break;
                    }
                }
            } else {
                //System.out.println("SocketList is empty!");
            }


        }

    }
}
