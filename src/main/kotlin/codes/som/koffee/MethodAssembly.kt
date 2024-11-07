package codes.som.koffee

import codes.som.koffee.insns.InstructionAssembly
import codes.som.koffee.labels.LabelRegistry
import codes.som.koffee.labels.LabelScope
import codes.som.koffee.sugar.ModifiersAccess
import codes.som.koffee.sugar.TypesAccess
import codes.som.koffee.types.TypeLike
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TryCatchBlockNode

/**
 * A higher-level representation of ASM's [MethodNode].
 */
public class MethodAssembly(public val node: MethodNode) : InstructionAssembly, TryCatchContainer, LabelScope, ModifiersAccess, TypesAccess {
    init {
        if (node.visibleAnnotations == null)
            node.visibleAnnotations = mutableListOf()
    }

    override val instructions: InsnList
        get() = node.instructions

    override val tryCatchBlocks: MutableList<TryCatchBlockNode>
        get() = node.tryCatchBlocks

    override val L: LabelRegistry = LabelRegistry(this)

    /**
     * The maximum stack size of this method. This is the maximum number of items that
     * can be pushed onto the stack before requiring the removal of items from the stack.
     */
    public var maxStack: Int
        get() = node.maxStack
        set(value) { node.maxStack = value }

    /**
     * The maximum number of local variables of this method. This defines
     * the highest slot index used for local variables.
     */
    public var maxLocals: Int
        get() = node.maxLocals
        set(value) { node.maxLocals = value }

    public fun annotation(type: TypeLike, vararg arguments: Pair<String, Any>): AnnotationNode {
        val annotationNode = AnnotationNode(ASM9, coerceType(type).className)
        for ((name, value) in arguments)
            annotationNode.visit(name, value)
        node.visibleAnnotations.add(annotationNode)
        return annotationNode
    }
}

/**
 * Get a [MethodAssembly] from this MethodNode, and return the modified node.
 */
public fun MethodNode.koffee(routine: MethodAssembly.() -> Unit): MethodNode {
    val assembly = MethodAssembly(this)
    routine(assembly)
    return assembly.node
}
