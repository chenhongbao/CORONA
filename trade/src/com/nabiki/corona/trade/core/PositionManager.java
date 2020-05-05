package com.nabiki.corona.trade.core;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class PositionManager {
	private final Map<String, PositionEngine> positions = new ConcurrentHashMap<>();
	private final Path directory;
	private final RuntimeInfo runtime;
	private final DataFactory factory;
	
	// Settlement mark.
	private boolean isSettled = false;
	
	public PositionManager(Path dir, RuntimeInfo runtime, DataFactory factory) {
		this.directory = dir;
		this.runtime = runtime;
		this.factory = factory;
		
		// TODO try initialize positions
	}
	
	public PositionEngine getPositon(String symbol) {
		return this.positions.get(symbol);
	}
	
	public void setPosition(String symbol) throws KerError {
		// Set new position engine.
		this.positions.put(symbol, new PositionEngine(symbol, this.runtime, null, this.factory));
	}
	
	public Collection<PositionEngine> positions() {
		return this.positions.values();
	}
	
	/**
	 * Settle position and write settlement result to disk. The file structures are below:<p>
	 * <code>
	 * - root\<br>
	 * &nbsp;&nbsp;- positions\<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- 20200505\<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 0\<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- origin position<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- locked\<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 0<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 1<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 2<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- closed\<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 0<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 1<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 2<br>
	 *</code>
	 * 
	 * @throws KerError
	 */
	public void settle() throws KerError {
		// After settlement, the positions are in settled status with valid settlement price and update margin/profits.
		for (var p : this.positions.values()) {
			var tick = this.runtime.lastTick(p.symbol());
			if (tick == null || !Utils.validPrice(tick.settlementPrice()))
				throw new KerError("Settlement price not ready: " + tick.symbol());
		}
		
		// All settlement prices must be ready before settling.
		var dirDate = subDirByDate(this.directory);
		for (var p : this.positions.values()) {
			p.settle(this.runtime.lastTick(p.symbol()).settlementPrice());
			p.write(new PositionFile(p.symbol(), subDirBySymbol(dirDate, p.symbol()), this.runtime, this.factory));
		}
		
		this.isSettled = true;
	}
	
	
	
	public void init() throws KerError {
		var dir = findLastPosDir(this.directory);
		for (var p : this.positions.values()) {
			p.read(new PositionFile(p.symbol(), subDirBySymbol(dir, p.symbol()), this.runtime, this.factory));
			p.init();
		}
		
		// TODO Remove zero volume position.
		
		this.isSettled = false;
	}
	
	public boolean isSettled() {
		return this.isSettled;
	}
	
	// Find the last, also biggest date directory.
	private Path findLastPosDir(Path root) throws KerError {
		var dirNames = Utils.getFileNames(root, false);
		if (dirNames == null || dirNames.length == 0)
			throw new KerError("Can't find directory under root: " + root);
		
		var dirs = new LinkedList<String>();
		for (var n : dirNames)
			dirs.add(n);
		// Sort in ascending order.
		Collections.sort(dirs);
		// Return last, also biggest element.
		Path ret = Path.of(root.toAbsolutePath().toString(), dirs.peekLast());
		Utils.ensureDir(ret);
		return ret;
	}
	
	// Create sub directory under root with a name noted by current date.
	private Path subDirByDate(Path root) throws KerError {
		var timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		var path = Path.of(root.toAbsolutePath().toString(), timeStamp);
		Utils.ensureDir(path);
		return path;
	}
	
	// Create sub directory under root with symbol as name.
	private Path subDirBySymbol(Path root, String symbol) throws KerError {
		Path path = Path.of(root.toAbsolutePath().toString(), symbol);
		Utils.ensureDir(path);
		return path;
	}
}