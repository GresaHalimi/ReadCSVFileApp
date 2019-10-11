package com.example.readcsvfileapp


import android.view.ViewGroup
import com.example.readcsvfileapp.repository.User
import com.example.readcsvfileapp.repository.UsersRepository
import com.example.readcsvfileapp.repository.UsersRepositoryImpl
import com.example.readcsvfileapp.main.UsersPresenter
import com.example.readcsvfileapp.main.UserItemView
import com.example.readcsvfileapp.main.UsersView
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*


class UsersPresenterImplTest {

    @Mock
    private lateinit var mockView: UsersView
    @Mock
    private lateinit var mockUsersEngineImpl: UsersRepository
    @Mock
    private lateinit var mockUserItemView: UserItemView
    @Mock
    private lateinit var mockViewGroup: ViewGroup

    private lateinit var subject: UsersPresenter

    @Before
    fun setupData() {
        MockitoAnnotations.initMocks(this)
        subject = UsersPresenter(mockUsersEngineImpl)
        subject.attachedView(mockView)
    }

    @Test
    fun itShouldShowProgressBarAndFetchUsers_When_NoUsers_On_OnRefresh() {
        subject.onRefresh()

        verify(mockView, times(2)).showProgressBar()
        verify(mockUsersEngineImpl, times(1)).fetchUsers()
    }

    @Test
    fun itShouldShowProgressBarAndFetchUsers_When_HasUsers_On_OnRefresh() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        subject.users = userList
        subject.onRefresh()

        verify(mockView, times(1)).showSwipeRefresh()
        verify(mockUsersEngineImpl, times(1)).fetchUsers()
    }

    @Test
    fun itShouldNotFetchData_When_HasCashedData_On_AttachedView() {
        val mockUserView = mock(UsersView::class.java)
        val mockEngine = mock(UsersRepository::class.java)
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        `when`(mockEngine.getUsers()).thenReturn(userList)

        val sub = UsersPresenter(mockEngine)
        sub.attachedView(mockUserView)

        verify(mockUserView, never()).showSwipeRefresh()
        verify(mockEngine, never()).fetchUsers()
    }

    @Test
    fun itShouldUnregisterEngine_On_onDetachView() {
        subject.detachView()

        verify(mockUsersEngineImpl, times(1)).onCancelWorkerScope()
        verify(mockUsersEngineImpl, times(1)).unRegister(subject)
    }

    @Test
    fun itShouldFetchUsers_On_RequestData() {
        subject.requestData()

        verify(mockUsersEngineImpl, times(1)).fetchUsers()
    }

    @Test
    fun itShouldShowSwipeRefresh_When_HasUsers_On_ShowLoading() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        subject.users = userList

        subject.showLoading()

        verify(mockView, times(1)).showSwipeRefresh()
    }

    @Test
    fun itShouldShowProgressBar_When_NoUsers_On_ShowLoading() {
        subject.showLoading()

        verify(mockView, times(2)).showProgressBar()
    }

    @Test
    fun itShouldDismissSwipeRefresh_When_HasUsers_On_DismissLoading() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        subject.users = userList

        subject.dismissLoading()

        verify(mockView, times(1)).dismissSwipeRefresh()
    }

    @Test
    fun itShouldDismissSwipeRefreshAndDismissProgressBar_When_NoUsers_On_DismissLoading() {
        subject.dismissLoading()

        verify(mockView, times(1)).dismissSwipeRefresh()
        verify(mockView, times(1)).dismissProgressBar()
    }

    @Test
    fun itShouldReloadContentView_On_OnFetchUsersSuccess() {
        subject.onFetchUsersSuccess(arrayListOf())

        verify(mockView, times(1)).reloadContentView()
    }

    @Test
    fun itShouldReturnDataSource_On_GetDataSource() {
        assertNotNull(subject.getDataSource())
    }

    @Test
    fun itShouldReturnUserSize_On_GetNumberOfItems() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        subject.users = userList

        assertEquals(userList.size, subject.getNumberOfItems())
    }

    @Test
    fun itShouldReturnItemView_On_OnCreateItemView() {
        subject.onCreateItemView(mockViewGroup)

        verify(mockView, times(1)).getUserItemView()
    }

    @Test
    fun itShouldUpdateItemView_When_DateOfBirthIsNull_On_OnItemViewCreated() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        subject.users = userList
        val item = 0

        subject.onItemViewCreated(mockUserItemView, item)

        verify(mockUserItemView, times(1)).setName(userList[item].firstname)
        verify(mockUserItemView, times(1)).setSurname(userList[item].surname)
        verify(mockUserItemView, times(1)).setIssueCount(userList[item].issueCount)
        verify(mockUserItemView, times(1)).setBirthDate(userList[item].dateOfBirth)
    }

    @Test
    fun itShouldUpdateItemView_When_DateOfBirthIsNotNull_On_OnItemViewCreated() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, Date()))
        subject.users = userList
        val item = 0

        subject.onItemViewCreated(mockUserItemView, item)

        verify(mockUserItemView, times(1)).setName(userList[item].firstname)
        verify(mockUserItemView, times(1)).setSurname(userList[item].surname)
        verify(mockUserItemView, times(1)).setIssueCount(userList[item].issueCount)
        verify(mockUserItemView, times(1)).setBirthDate(userList[item].dateOfBirth)
    }


    @Test
    fun itShouldDismissLoadingAndShowSnackBar_When_UserListIsNotEmpty_On_onFetchUsersFailure() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))

        `when`(mockUsersEngineImpl.getUsers()).thenReturn(userList)

        val throwable = Throwable("Error")
        subject.onFetchUsersFailure(throwable)


        verify(mockView, times(1)).dismissSwipeRefresh()
        verify(mockView, times(1)).showSnackbar(throwable.message.toString())
    }

    @Test
    fun itShouldDismissLoadingAndShowSnackBar_When_UserListIsEmpty_On_onFetchUsersFailure() {
        `when`(mockUsersEngineImpl.getUsers()).thenReturn(arrayListOf())

        val throwable = Throwable("Error")
        subject.onFetchUsersFailure(throwable)


        verify(mockView, times(1)).dismissSwipeRefresh()
        verify(mockView, times(1)).dismissProgressBar()
        verify(mockView, times(1)).showErrorView(throwable.message.toString())
    }

    @Test
    fun itShouldRequestData_When_NoCashedUsers_On_OnFetchCachedUsersSuccess() {
        subject.onFetchCachedUsersSuccess(arrayListOf())

        verify(mockUsersEngineImpl, times(1)).fetchUsers()
    }

    @Test
    fun itShouldDismissSwipeRefreshAndReloadContentView_When_HasCashedUsers_On_OnFetchCachedUsersSuccess() {
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))

        subject.onFetchCachedUsersSuccess(userList)

        verify(mockView, times(1)).dismissSwipeRefresh()
        verify(mockView, times(1)).reloadContentView()
    }
}