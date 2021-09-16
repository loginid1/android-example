### Android Example

## Requirements

- Android Studio
- Go

## env

A .env file is needed in the `/server` directory with the following variables:

```
BASE_URL=                            #This is the base url value found on the dashboard.
PRIVATE_KEY=                         #This is the private key associated with client ID.
```

You would also have to edit `ApplicationData` and change the `BASE_URL` and `CLIENT_ID` variables as well.

```
public class ApplicationData extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final String BASE_URL = "<BASE_URL>";
        final String CLIENT_ID = "<CLIENT_ID>";

        LoginApi.client().configure(this, CLIENT_ID, BASE_URL);
    }
}
```

## How to Run

```
git clone https://github.com/loginid1/android-example.git
cd android-example
```

### Installing/Running Backend Server

```
cd ./server/
go run main.go
```

### Running the Application

Use Android Studio or any other tool that can run this application on a emulator. This demo was designed to run on a emulator. If you choose to run it on a real device, change the `baseURL` in the `Service` to whatever IP/hostname and port needed to network with your phone.

### Registering a Fingerprint on Emulator

There is minor differences between emulators, but the process is almost the same. You would have to go the emulator settings and set up the fingerprint. Go to `Fingerprint` option under `Extended Controls` to "touch" and register the fingerprint. Here is an example with Pixel 3 emulator:

![Registering Fingerprint](fingerprint.gif)

From here you can run the application in an emulator and use the `Fingerprint` option in `Extended Controls` to "touch" during the biometric prompt.
