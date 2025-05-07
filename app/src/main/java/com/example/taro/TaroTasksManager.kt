package com.example.taro

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.example.taro.Adapters.TaskListComposeAdapter
import com.example.taro.Dao.TaroDb
import com.example.taro.Dao.UserTaskDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class TaroTasksManager {
    private lateinit var taskListAdapter : TaskListComposeAdapter;
    private lateinit var  TaskListRecyclerView : RecyclerView


    fun insertUserTasks(context: Context, userTasks : List<UserTaskDb>){
        taskListAdapter = TaskListComposeAdapter(emptyList());
        var newFetchedData : List<Pair<String, Boolean>>;
        CoroutineScope(Dispatchers.IO).launch {
            // before inserting take the task information and generate the score
            val dao = (context.applicationContext as TaroApplication).db.taroDao()
            //spread operator for vararg
            dao.insertAll(*userTasks.toTypedArray())

            Log.e("DATABASE","completed insertion")

            val newData = userTasks[0].dueDate?.let { getTasksByDate(context, it) }

            newFetchedData = newData?.map { it.name to it.isCompleted }!!

        }
    }

    fun updateTaskStatus(context: Context,status: Boolean, currTaskId:Int){
        CoroutineScope(Dispatchers.IO).launch {
            val dao = (context.applicationContext as TaroApplication).db.taroDao()
            //
            val defaultCompletion : Int = 0
            dao.updateTaskStatus(status,taskId=currTaskId, completionTime = defaultCompletion);


        }
    }

    fun setTasksTaroScore(context:Context,date: String,moodMultiplier:Int){

        CoroutineScope(Dispatchers.IO).launch {
            val dao = (context.applicationContext as TaroApplication).db.taroDao()
            val fetchedTasks = getTasksByDate(context,date);

            fetchedTasks.forEach { task ->
                /** Wait for this to end **/
                if (task.taroScore == null) {
                    val generatedScore: Double = generateTaskScore(task, moodMultiplier, 10);
                    /** Updating taro score for this task **/
                    dao.setTaroScore(generatedScore, task.uid);
                }
            }

        }
    }

    suspend fun getTasksByDate(context: Context, date: String): List<UserTaskDb> {
        val dao = (context.applicationContext as TaroApplication).db.taroDao()

        return dao.fetchTasksByDate(date)
    }

    suspend fun getAllTasks(context: Context) : List<UserTaskDb>{
        val dao = (context.applicationContext as TaroApplication).db.taroDao()
        return dao.getAllTasks();
    }

    /** Returns a Scalar and creates the wanted score **/
    private fun generateTaskScore(task:UserTaskDb, moodMultiplier : Int , totalTasks : Int): Double {

        val taskDifficulty : Int = task.difficulty;
        val taskPriority : Int = task.priority;
        val taskUrgency: Int = task.urgency;
        val taskExpectedDuration : Double = task.expectedDuration;

        /** Combination of attributes */
        val attributesProductSum : Double = (taskDifficulty * taskPriority) + (taskDifficulty * taskUrgency) + (taskDifficulty * taskExpectedDuration) + (taskPriority * taskUrgency) + (taskPriority * taskExpectedDuration) + (taskUrgency * taskExpectedDuration)

        val finalScore : Double = (attributesProductSum *  moodMultiplier)/totalTasks;

        return finalScore;
    }

    fun insertPathData(context: Context,){}

}