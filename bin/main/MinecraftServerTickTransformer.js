function initializeCoreMod() {
	
	Opcodes = Java.type("org.objectweb.asm.Opcodes");
	InsnList = Java.type("org.objectweb.asm.tree.InsnList");
	FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
	
	return {
		"MinecraftServer.run": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.server.MinecraftServer",
				"methodName": "run",
				"methodDesc": "()V"
			},
			"transformer": function(methodNode) {
				var list = methodNode.instructions;
				for(var i = 0; i < list.size(); i++) {
					var insn = list.get(i);
					// if instruction is 'ldc'-instruction and value to be loaded is long 50L
					if(insn.getOpcode() == Opcodes.LDC && insn.cst == 50) {
						//	replace			ldc2_w		#1126		// long 50L
						//	with			getstatic	# ?			// Field me/gamma/tickwarp/TickWarp.TICK_LENGTH;
						list.set(insn, new FieldInsnNode(Opcodes.GETSTATIC, "me/gamma/tickwarp/TickWarp", "TICK_LENGTH", "J"));
					}
				}
				return methodNode;
			}
		}
	}
}