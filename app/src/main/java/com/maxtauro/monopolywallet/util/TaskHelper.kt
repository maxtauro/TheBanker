package com.maxtauro.monopolywallet.util

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.*
import java.util.concurrent.ExecutionException

/**
 *  TODO Add authoring and date
 */
@Deprecated("Do we ever need to use this class?", ReplaceWith("",""), DeprecationLevel.WARNING)
class TaskHelper {

    fun <T> getValueSynchronously(databaseRef: DatabaseReference): T {

        val tcs = TaskCompletionSource<T>()

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tcs.setResult(dataSnapshot.value.toString() as T)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                tcs.setException(databaseError.toException())
            }
        })

        var t = tcs.task

        try {
            Tasks.await(t)
        } catch (e: ExecutionException) {
            t = Tasks.forException(e)
        } catch (e: InterruptedException) {
            t = Tasks.forException(e)
        }

        if (t.isSuccessful) return t.result as T ?: "" as T //TODO is this cast correct?
        else {
            TODO("THROW SOME ERROR TO INDICATE WE COULDNT GET THE VALUE")
        }

    }

    @Deprecated("TODO investigate why this throws 'java.lang.IllegalStateException: Must not be called on the main application thread'")
    fun <T> executeTaskSynchronously(task: Task<T>) {

        try {
            Tasks.await(task)
        }
        catch (e: ExecutionException) {
            TODO("CATCH EXECUTION ERROR")
        }
        catch (e: InterruptedException) {
            TODO("CATCH Interruption ERROR")
        }

    }

}