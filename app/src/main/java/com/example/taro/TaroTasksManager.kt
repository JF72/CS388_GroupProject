package com.example.taro

import android.content.Context
import com.example.taro.Dao.TaroDb
import com.example.taro.Dao.UserTaskDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaroTasksManager {

    fun insertUserTasks(context: Context, userTasks : List<UserTaskDb>){
        CoroutineScope(Dispatchers.IO).launch {
            // before inserting take the task information and generate the score

            val dao = (context.applicationContext as TaroApplication).db.taroDao()
            //spread operator for vararg
            dao.insertAll(*userTasks.toTypedArray())
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

    fun setTasksTaroScore(context:Context,userTasks: List<UserTaskDb>,moodMultiplier:Int){
        CoroutineScope(Dispatchers.IO).launch {
            val dao = (context.applicationContext as TaroApplication).db.taroDao()

            userTasks.forEach({task ->
                /** Wait for this to end **/
                val generatedScore : Double = generateTaskScore(task,moodMultiplier,10);

                /** Updating taro score for this task **/
                dao.setTaroScore(generatedScore,task.uid);
            })

        }
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


}