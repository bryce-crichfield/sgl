
import scala.util.Failure

import scala.util.Success
import java.nio.ByteBuffer
package object core {
    // type Unsafe[+A] = cats.effect.IO[A]
    // val Unsafe = cats.effect.IO
    type Unsafe[+A] = scala.util.Try[A]
    val Unsafe = scala.util.Try

    type MutBuf[A] = scala.collection.mutable.ListBuffer[A]
    val MutBuf = scala.collection.mutable.ListBuffer

    type MutMap[K, V] = scala.collection.mutable.HashMap[K, V]
    val MutMap = scala.collection.mutable.HashMap
    object Util {
        def throwOn(predicate: => Boolean)(debug: String = ""): Unsafe[Unit] = {
            if predicate 
            then scala.util.Failure(new RuntimeException(debug))
            else scala.util.Success(())
        }


        def toFloatBuffer(matrix: org.joml.Matrix4f): java.nio.FloatBuffer = {
            val buffer = java.nio.ByteBuffer.allocateDirect(16 << 2)
                .order(java.nio.ByteOrder.nativeOrder())
                .asFloatBuffer()
            buffer.put(matrix.m00());
            buffer.put(matrix.m01());
            buffer.put(matrix.m02());
            buffer.put(matrix.m03());
            buffer.put(matrix.m10());
            buffer.put(matrix.m11());
            buffer.put(matrix.m12());
            buffer.put(matrix.m13());
            buffer.put(matrix.m20());
            buffer.put(matrix.m21());
            buffer.put(matrix.m22());
            buffer.put(matrix.m23());
            buffer.put(matrix.m30());
            buffer.put(matrix.m31());
            buffer.put(matrix.m32());
            buffer.put(matrix.m33());
            return buffer
        }

        def toArray(matrix: org.joml.Matrix4f): Array[Float] = {
            val array = new Array[Float](16)
            array.update(0, matrix.m00());
            array.update(1, matrix.m01());
            array.update(2, matrix.m02());
            array.update(3, matrix.m03());
            array.update(4, matrix.m10());
            array.update(5, matrix.m11());
            array.update(6, matrix.m12());
            array.update(7, matrix.m13());
            array.update(8, matrix.m20());
            array.update(9, matrix.m21());
            array.update(10, matrix.m22());
            array.update(11, matrix.m23());
            array.update(12, matrix.m30());
            array.update(13, matrix.m31());
            array.update(14, matrix.m32());
            array.update(15, matrix.m33());
            return array
        }

    }

    extension [A] (unsafe: Unsafe[A])
        def onFail(p: Throwable => Unit): Unsafe[A] = {
            unsafe match
                case f @ Failure(exception) => 
                    p(exception)
                    f
                case s @ Success(value) => s
            
        }



}
