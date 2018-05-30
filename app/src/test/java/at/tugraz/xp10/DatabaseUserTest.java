package at.tugraz.xp10;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.HashMap;

import at.tugraz.xp10.firebase.Database;
import at.tugraz.xp10.firebase.DatabaseListValueEventListener;
import at.tugraz.xp10.firebase.DatabaseValueEventListener;
import at.tugraz.xp10.firebase.ShoppingLists;
import at.tugraz.xp10.firebase.Users;
import at.tugraz.xp10.model.ModelBase;
import at.tugraz.xp10.model.ShoppingList;
import at.tugraz.xp10.model.User;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ Database.class, FirebaseAuth.class})

public class DatabaseUserTest {

    private Database mockedDatabase;
    private FirebaseAuth mockedAuth;

    private static final HashMap<String, User> TestData;

    static {
        TestData = new HashMap<>();
        TestData.put("-LCcgnf6nfs4KOty-oAn", new User("foo", "foo", "foo"));
        TestData.put("-LCchNPtr43TgU0sflIo", new User("bar", "bar", "bar"));
        TestData.put("-LCchOPfUHwNBMw_oWT3", new User("baz", "baz", "baz"));
        TestData.put("-LCd31jsrRr_SH6q9JSL", new User("xxx", "xxx", "xxx"));
    }

    @Before
    public void before() {
        mockedDatabase = Mockito.mock(Database.class);
        PowerMockito.mockStatic(Database.class);
        when(Database.getInstance(Mockito.anyString())).thenReturn(mockedDatabase);

        mockedAuth = Mockito.mock(FirebaseAuth.class);
        PowerMockito.mockStatic(FirebaseAuth.class);
        when(FirebaseAuth.getInstance()).thenReturn(mockedAuth);
    }

    @Test
    public void getUsersTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                DatabaseListValueEventListener valueEventListener = (DatabaseListValueEventListener) invocation.getArguments()[1];
                valueEventListener.onNewData(TestData);

                return null;
            }
        }).when(mockedDatabase).getListOfValues(any(Class.class), any(DatabaseListValueEventListener.class));

        Users users = new Users();
        users.getUsers(new DatabaseListValueEventListener() {
            @Override
            public <T extends ModelBase> void onNewData(HashMap<String, T> data) {
                for (T c : data.values())
                {
                    Assert.assertTrue(TestData.containsValue((User) c));
                }
            }
        });
    }

    @Test
    public void getUserTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                DatabaseListValueEventListener valueEventListener = (DatabaseListValueEventListener) invocation.getArguments()[1];
                valueEventListener.onNewData(TestData);

                return null;
            }
        }).when(mockedDatabase).getListOfValues(any(Class.class), any(DatabaseListValueEventListener.class));

        Users users = new Users();
        users.getUser("key", new DatabaseValueEventListener() {

            @Override
            public <T extends ModelBase> void onNewData(T data) {
                Assert.assertTrue(TestData.containsValue(data));
            }
        });
    }
    @Test
    public void getCurrentUserTest() {

        Users users = Mockito.mock(Users.class);
        Mockito.doCallRealMethod().when(users).getCurrentUser( Mockito.any(DatabaseValueEventListener.class));
        users.getCurrentUser(new DatabaseValueEventListener() {
            @Override
            public <T extends ModelBase> void onNewData(T data) {
                Assert.assertTrue(TestData.containsValue(data));
            }
        });
    }


    @Test
    public void setUserTest() {
        Users users = new Users();
        User test = new User("foo", "foo", "hallo");
        users.setUser("test", test);

        verify(mockedDatabase).setValue("test",test);
    }


    @Test
    public void addShoppingListToUserTest() {
        Users users = new Users();
        users.addShoppingListToUser("test", "test");

        HashMap<String, Object> newList = new HashMap<>();
        newList.put("test", true);

        verify(mockedDatabase).setValue("testshoppinglists/", newList);
    }

    @Test
    public void getCurrentUserIDTest() {

        Users users = Mockito.mock(Users.class);
        when(users.getCurrentUserID()).thenReturn("test");
        //TODO: finish Testcase
    }

    @Test
    public void setCurrentUser() {
        //TODO: finish Testcase
    }


}
