package com.example.readcsvfileapp


import android.view.ViewGroup
import com.example.readcsvfileapp.engine.User
import com.example.readcsvfileapp.engine.UsersEngineImpl
import com.example.readcsvfileapp.ui.UserItemView
import com.example.readcsvfileapp.ui.UsersView
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*


class UsersPresenterImplTest {

    @Mock
    private lateinit var mockView: UsersView
    @Mock
    private lateinit var mockUsersEngineImpl: UsersEngineImpl
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
    fun itShouldFetchData_On_AttachedView(){

        verify(mockUsersEngineImpl, times(1)).fetchUsers()
    }

    @Test
    fun itShouldReloadData_On_UpdateView(){
        subject.updateView()

        verify(mockView, times(1)).reloadContentView()
    }

    @Test
    fun itShouldFetchData_On_OnRefresh(){
        subject.onRefresh()

        verify(mockUsersEngineImpl, times(2)).fetchUsers()
    }

    @Test
    fun itShouldUnregisterEngine_On_onDetachView() {
        subject.detachView()

        verify(mockUsersEngineImpl, times(1)).unRegister(subject)
    }

    @Test
    fun itShouldFetchUsers_On_RequestData() {
        subject.requestData()

        verify(mockUsersEngineImpl, times(2)).fetchUsers()
    }

    @Test
    fun itShouldReloadContentView_On_OnFetchUsersSuccess(){
        subject.onFetchUsersSuccess(arrayListOf())

        verify(mockView, times(1)).reloadContentView()
    }

    @Test
    fun itShouldReturnDataSource_On_GetDataSource(){
        assertNotNull(subject.getDataSource())
    }

    @Test
    fun itShouldReturnUserSize_On_GetNumberOfItems(){
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        subject.users = userList

        assertEquals(userList.size, subject.getNumberOfItems())
    }

    @Test
    fun itShouldReturnItemView_On_OnCreateItemView(){
        subject.onCreateItemView(mockViewGroup)

        verify(mockView, times(1)).getUserItemView()
    }

    @Test
    fun itShouldUpdateItemView_When_DateOfBirthIsNull_On_OnItemViewCreated(){
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, null))
        subject.users = userList
        val item = 0

        subject.onItemViewCreated(mockUserItemView, item)

        verify(mockUserItemView, times(1)).setName(userList.get(item).firstname)
        verify(mockUserItemView, times(1)).setSurname(userList.get(item).surname)
        verify(mockUserItemView, times(1)).setIssueCount(userList.get(item).issueCount)
        verify(mockUserItemView, times(1)).setBirthDate(userList.get(item).dateOfBirth)
    }

    @Test
    fun itShouldUpdateItemView_When_DateOfBirthIsNotNull_On_OnItemViewCreated(){
        val userList = arrayListOf(User(1, "Gresa", "Halimi", 0, Date()))
        subject.users = userList
        val item = 0

        subject.onItemViewCreated(mockUserItemView, item)

        verify(mockUserItemView, times(1)).setName(userList.get(item).firstname)
        verify(mockUserItemView, times(1)).setSurname(userList.get(item).surname)
        verify(mockUserItemView, times(1)).setIssueCount(userList.get(item).issueCount)
        verify(mockUserItemView, times(1)).setBirthDate(userList.get(item).dateOfBirth)
    }
}