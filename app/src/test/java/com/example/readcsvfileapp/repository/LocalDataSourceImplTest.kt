package com.example.readcsvfileapp.repository

import android.content.Context
import android.content.res.Resources
import com.example.readcsvfileapp.R
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LocalDataSourceImplTest{

    @Mock
    private lateinit var mockContext: Context
    @Mock
    private lateinit var mockResources: Resources

    private lateinit var dataSource: LocalDataSourceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun itShouldReadDataFromCSVFile_When_FileIsCorrect_On_readData(){
        val resourceInputFile = "/issues_test.csv"
        val url = javaClass.getResource(resourceInputFile)
        val stream = url?.openStream()
        `when`(mockContext.resources).thenReturn(mockResources)
        `when`(mockContext.resources.openRawResource(R.raw.issues)).thenReturn(stream)

        dataSource = LocalDataSourceImpl(mockContext)
        val users = dataSource.readData()

        assertEquals(3, users?.size)
    }

    @Test
    fun itShouldReadDataFromCSVFile_When_IssueNumberIsString_On_readData(){
        val resourceInputFile = "/issues_test_icorrect_issue.csv"
        val url = javaClass.getResource(resourceInputFile)
        val stream = url?.openStream()
        `when`(mockContext.resources).thenReturn(mockResources)
        `when`(mockContext.resources.openRawResource(R.raw.issues)).thenReturn(stream)

        dataSource = LocalDataSourceImpl(mockContext)
        val users = dataSource.readData()

        assertEquals(3, users?.size)
    }

    @Test(expected = LocalDataSourceImpl.FileStructureException::class)
    fun itShouldThrowFileStructureException_When_DateHasWrongFormat_On_readData(){
        val resourceInputFile = "/issues_test_date_format_incorrect.csv"
        val url = javaClass.getResource(resourceInputFile)
        val stream = url?.openStream()
        `when`(mockContext.resources).thenReturn(mockResources)
        `when`(mockContext.resources.openRawResource(R.raw.issues)).thenReturn(stream)

        dataSource = LocalDataSourceImpl(mockContext)
        dataSource.readData()
    }

    @Test(expected = LocalDataSourceImpl.FileStructureException::class)
    fun itShouldThrowFileStructureException_When_ColumnNumberIsIncorrect_On_readData(){
        val resourceInputFile = "/issues_test_incorrect_data.csv"
        val url = javaClass.getResource(resourceInputFile)
        val stream = url?.openStream()
        `when`(mockContext.resources).thenReturn(mockResources)
        `when`(mockContext.resources.openRawResource(R.raw.issues)).thenReturn(stream)

        dataSource = LocalDataSourceImpl(mockContext)
        dataSource.readData()
    }

}