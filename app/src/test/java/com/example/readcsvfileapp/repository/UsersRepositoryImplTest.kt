package com.example.readcsvfileapp.repository

import com.example.readcsvfileapp.database.AppDatabase
import com.example.readcsvfileapp.database.UserDao
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*


class UsersRepositoryImplTest {

    @Mock
    private lateinit var mockDatabase: AppDatabase
    @Mock
    private lateinit var mockUserDao: UserDao
    @Mock
    private lateinit var mockLocalDataSource: LocalDataSource
    @Mock
    private lateinit var mockFetchCachedUsersCallback: UsersRepository.FetchCachedUsersCallback
    @Mock
    private lateinit var mockFetchUsersCallback: UsersRepository.FetchUsersCallback

    private lateinit var testCoroutineContextProvider: TestCoroutineContentProvider
    private lateinit var subject: UsersRepositoryImpl


    @Before
    fun setupData() {
        MockitoAnnotations.initMocks(this)
        `when`(mockDatabase.userDao()).thenReturn(mockUserDao)
        testCoroutineContextProvider = TestCoroutineContentProvider()
        subject =
            UsersRepositoryImpl(mockDatabase, testCoroutineContextProvider, mockLocalDataSource)
    }

    @Test
    fun itShouldReadCachedData_On_GetCachedData() = runBlocking {
        val user1 = User(1, "User1", "User1Surname", 1, null)
        val user2 = User(2, "User2", "User2Surname", 3, Date())
        val users = arrayListOf<User>()
        users.add(user1)
        users.add(user2)
        `when`(mockDatabase.userDao().allUsers()).thenReturn(users)
        subject.register(mockFetchCachedUsersCallback)

        subject.getCachedData()

        verify(mockFetchCachedUsersCallback).onFetchCachedUsersSuccess(users)
    }

    @Test
    fun itShouldCallOnFetchUsersSuccess_When_FileIsCorrect_On_FetchUsers() = runBlocking {
        subject.register(mockFetchUsersCallback)
        subject.fetchUsers()

        verify(mockFetchUsersCallback).onFetchUsersSuccess(arrayListOf())
    }

    @Test
    fun itShouldCallOnFetchUsersFailure_When_ExceptionOccurredTryingToReadTheFile_On_FetchUsers() = runBlocking {
        val errorMessage = "Error"
        val fileStructureException = LocalDataSourceImpl.FileStructureException(errorMessage)
        `when`(mockLocalDataSource.readData()).thenThrow(fileStructureException)
        subject.register(mockFetchUsersCallback)
        subject.fetchUsers()

        verify(mockFetchUsersCallback).onFetchUsersFailure(fileStructureException)
    }
}