package com.nabiki.corona.trade.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.DefaultDataCodec;
import com.nabiki.corona.kernel.api.DataCodec;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class PositionFile {
	private final String symbol;
	private final Path directory;
	private final RuntimeInfo runtime;
	private final DataFactory factory;

	// Data codec.
	private final DataCodec codec = DefaultDataCodec.create();

	public PositionFile(String symbol, Path dir, RuntimeInfo info, DataFactory factory) {
		this.symbol = symbol;
		this.directory = dir;
		this.runtime = info;
		this.factory = factory;
	}

	public String symbol() {
		return this.symbol;
	}

	public List<RuntimePositionDetail> read() throws KerError {
		// Get sub-directories.
		var directories = Utils.getFileNames(this.directory, false);
		
		if (directories == null)
			throw new KerError("Directory not exists: " + this.directory.toAbsolutePath());
		
		var ret = new LinkedList<RuntimePositionDetail>();
		// No sub-dirs found.
		if (directories.length == 0)
			return ret;
		
		for (String d : directories) {
			var path = Path.of(this.directory.toAbsolutePath().toString(), d);
			RuntimePositionDetail r = readRuntime(path);
			
			// Filter the runtime. Don't load empty position detail.
			if (r.available().volume() > 0)
				ret.add(r);
		}
		
		return ret;
	}

	private RuntimePositionDetail readRuntime(Path path) throws KerError {
		// Read origin.
		var origin = readDetails(path);
		if (origin.isEmpty())
			throw new KerError("Origin detail not found: " + path.toAbsolutePath().toString());
		if (origin.size() > 1)
			throw new KerError("Ambiguious details: " + path.toAbsolutePath().toString());
		
		// Read locked.
		var locked = readDetails(Path.of(path.toAbsolutePath().toString(), "locked"));
		// Read closed.
		var closed = readDetails(Path.of(path.toAbsolutePath().toString(), "closed"));
		
		return new RuntimePositionDetail(origin.get(0), locked, closed, this.runtime, this.factory);
	}
	
	private List<KerPositionDetail> readDetails(Path p) throws KerError {
		List<KerPositionDetail> ret = new LinkedList<>();
		for (var f: Utils.getFileNames(p, true)) {
			var fp =Path.of(p.toAbsolutePath().toString(), f);
			ret.add(readDetail(fp));
		}
		
		return ret;
	}
	
	private KerPositionDetail readDetail(Path p) throws KerError {
		return this.codec.decode(Utils.readFile(p), KerPositionDetail.class);
	}

	public void write(List<RuntimePositionDetail> ps) throws KerError {
		if (ps.size() == 0)
			return;
		
		int idx = 0;
		for (var p : ps)
			writeRuntime(p, idx++);
	}

	private void writeRuntime(RuntimePositionDetail p, int idx) throws KerError {
		var origin = p.origin();
		var locked = p.locked();
		var closed = p.closed();

		// Root by idx.
		var root =getDetailRootByIdx(idx);
		
		// Write origin.
		writeDetail(root, origin, 0);
		// Locked position details.
		writeDetails(root, "/locked", locked);
		// Closed position details.
		writeDetails(root, "/closed", closed);
	}
	
	private Path getDetailRootByIdx(int idx) throws KerError {
		var path = Path.of(this.directory.toAbsolutePath().toString(), Integer.toString(idx));
		Utils.ensureDir(path);
		return path;
	}

	private void writeDetails(Path dir, String subDir, List<KerPositionDetail> ps) throws KerError {
		if (ps.size() == 0)
			return;

		// Build sub directory.
		if (!subDir.startsWith("/") && !subDir.startsWith("\\"))
			subDir = "/" + subDir;
		if (!subDir.endsWith("/") && !subDir.endsWith("\\"))
			subDir += "/";

		var root = Path.of(dir.toAbsolutePath().toString(), subDir);
		Utils.ensureDir(root);

		int idx = 0;
		for (var p : ps)
			writeDetail(root, p, idx++);
	}

	private void writeDetail(Path dir, KerPositionDetail d, int idx) throws KerError {
		byte[] bytes = this.codec.encode(d);
		var file = Path.of(dir.toAbsolutePath().toString(), Integer.toString(idx)).toFile();
		try {
			if (!file.exists() || !file.isFile())
				file.createNewFile();
		} catch (IOException e) {
			throw new KerError("Fail creating position detail data file: " + file.getAbsolutePath(), e);
		}
		try {
			var os = new FileOutputStream(file);
			os.write(bytes);
			os.flush();
			os.close();
		} catch (IOException e) {
			throw new KerError("Fail writing position detail data to file: " + file.getAbsolutePath(), e);
		}
	}
}
