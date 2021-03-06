package com.domkow.kloshard.Utils;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import mk.gdx.firebase.GdxFIRAuth;
import mk.gdx.firebase.GdxFIRDatabase;
import mk.gdx.firebase.auth.GdxFirebaseUser;
import mk.gdx.firebase.callbacks.AuthCallback;
import mk.gdx.firebase.callbacks.CompleteCallback;
import mk.gdx.firebase.callbacks.DataCallback;
import mk.gdx.firebase.listeners.DataChangeListener;

public class FireBaseManager {
    private static volatile FireBaseManager instance;
    public GdxFIRAuth auth;
    public GdxFIRDatabase db;
    public boolean loggedIn = false;
    public static boolean accCreated = false;
    public static boolean attemptToSignIn = false;
    public static boolean accNotCreated = false;
    public boolean skin2 = false;
    public boolean skin3 = false;
    public boolean loginFail;

    public static FireBaseManager instance() {
        FireBaseManager result = instance;
        if (result == null) {
            synchronized (GdxFIRDatabase.class) {
                result = instance;
                if (result == null) {
                    instance = result = new FireBaseManager();
                }
            }
        }
        return result;
    }

    public FireBaseManager() {
        this.auth = GdxFIRAuth.instance();
        this.db = GdxFIRDatabase.instance();
        getUserData();
    }

    private void createDataChangeListener() {
        if (auth.getCurrentUser() != null) {
            db.inReference("users/" + auth.getCurrentUser().getUserInfo().getUid())
                    .onDataChange(HashMap.class, new DataChangeListener<HashMap>() {
                        @Override
                        public void onChange(HashMap newValue) {
                            skin2 = (Boolean) newValue.get("skin2");
                            skin3 = (Boolean) newValue.get("skin3");
                            Gdx.app.log("Database", "Updated");
                        }

                        @Override
                        public void onCanceled(Exception e) {
                            Gdx.app.log("Database", e.getMessage());
                        }
                    });
        }
    }

    public void getUserData() {
        if (auth.getCurrentUser() != null) {
            db.inReference("users/" + auth.getCurrentUser().getUserInfo().getUid())
                    .readValue(HashMap.class, new DataCallback<HashMap>() {
                        @Override
                        public void onData(HashMap data) {
                            Gdx.app.log("onData:", "START retriving");
                            try {
                                skin2 = (Boolean) data.get("skin2");
                                skin3 = (Boolean) data.get("skin3");
                            } catch (Exception e) {
                                Gdx.app.log("retriving data", e.getMessage());
                            }
                            Gdx.app.log("onData:", "STOP");
                        }

                        @Override
                        public void onError(Exception e) {
                            Gdx.app.log("read database result", e.getMessage());
                        }
                    });
        }
    }

    public void updateUserCredentials(HashMap<String, Object> data) {
        Gdx.app.log("Account Creation Result", "success");
        if (auth.getCurrentUser() != null) {
            db.inReference("users/" + auth.getCurrentUser().getUserInfo().getUid())
                    .updateChildren(data, new CompleteCallback() {
                        @Override
                        public void onSuccess() {
                            Gdx.app.log("Database:", "user skin field updated");
                        }

                        @Override
                        public void onError(Exception e) {
                            Gdx.app.log("Database", e.getMessage());
                        }
                    });
        }
    }

    public void signInUser(final String email, char[] psswd) {
        auth.signInWithEmailAndPassword(email, psswd, new AuthCallback() {
            @Override
            public void onSuccess(GdxFirebaseUser user) {
                attemptToSignIn = false;
                loggedIn = true;
                Gdx.app.log("Login result", "success");
                createDataChangeListener();
            }

            @Override
            public void onFail(Exception e) {
                Gdx.app.log("Login result", "fail");
                attemptToSignIn = false;
                loginFail=true;
            }

        });
    }

    public void createUser(String email, char[] psswd) {
        auth.createUserWithEmailAndPassword(email, psswd, new AuthCallback() {
            @Override
            public void onSuccess(GdxFirebaseUser user) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("skin1", true);
                map.put("skin2", false);
                map.put("skin3", false);
                db.inReference("users/" + user.getUserInfo().getUid())
                        .updateChildren(map, new CompleteCallback() {
                            @Override
                            public void onSuccess() {
                                Gdx.app.log("Database:", "user skin field created");
                                accCreated = true;
                            }

                            @Override
                            public void onError(Exception e) {
                                Gdx.app.log("Database", e.getMessage());
                                accNotCreated = true;
                            }
                        });
                Gdx.app.log("Account Creation Result", "success");
            }

            @Override
            public void onFail(Exception e) {
                Gdx.app.log("Creation account result", "fail");
                Gdx.app.log("Exception", e.getMessage());
            }
        });


    }

}
