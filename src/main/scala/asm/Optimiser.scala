package asm

import asm.instructions._
import asm.instructionset._
import asm.registers._
import asm.utilities._
import scala.util.control.Breaks._

object Optimiser {

  def optimise(instructions: IndexedSeq[Instruction]): IndexedSeq[Instruction] = {
    var optIns = instructions.toBuffer

    // optIns.flatMap(i, j => optimiseChecks(i, j))

    for (i <- 0 until instructions.length - 1) {
      // First two instructions to retrieve and compare.
      var current = optIns.remove(i)
      var next = optIns.remove(i)
      breakable {
        current match {
          // Removing redundant load.
          case store: Store if next.isInstanceOf[Load] =>
            val load = next.asInstanceOf[Load]
            if (load.src.isDefined && store.src.isDefined && store.dest == load.src.get
              && store.src.get == load.dest) {
              optIns.insert(i, store)
              break
            }

          // Instead of loading then moving to another register, load directly into that register.
          case load: Load if next.isInstanceOf[Move] =>
            val move = next.asInstanceOf[Move]
            if (load.dest == move.src.asInstanceOf[ShiftedRegister].register) {
              optIns.insert(i, Load(load.condition, load.asmType, move.dest, load.src, load.offset,
                load.registerWriteBack, load.loadable, load.label))
              break
            }

          // Redundant POP, not necessary to POP anything twice.
          case pop: Pop if next.isInstanceOf[Pop] =>
            val nextPop = next.asInstanceOf[Pop]
            optIns.insert(i, pop)
            break

          case _ =>
        }
      }


      // Add instructions back if no optimisations have been found.
      optIns.insert(i, current)
      optIns.insert(i, next)
    }

    // Don't want to have to have the cost of conversion.
    optIns.toIndexedSeq
  }

}
