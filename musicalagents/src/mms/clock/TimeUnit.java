package mms.clock;

public enum TimeUnit {

    NANOSECONDS {
        public double toNanos(double d)   { return d; }
        public double toMicros(double d)  { return d/(C1/C0); }
        public double toMillis(double d)  { return d/(C2/C0); }
        public double toSeconds(double d) { return d/(C3/C0); }
        public double convert(double d, TimeUnit u) { return u.toNanos(d); }
    },
    MICROSECONDS {
        public double toNanos(double d)   { return x(d, C1/C0, MAX/(C1/C0)); }
        public double toMicros(double d)  { return d; }
        public double toMillis(double d)  { return d/(C2/C1); }
        public double toSeconds(double d) { return d/(C3/C1); }
        public double convert(double d, TimeUnit u) { return u.toMicros(d); }
    },
    MILLISECONDS {
        public double toNanos(double d)   { return x(d, C2/C0, MAX/(C2/C0)); }
        public double toMicros(double d)  { return x(d, C2/C1, MAX/(C2/C1)); }
        public double toMillis(double d)  { return d; }
        public double toSeconds(double d) { return d/(C3/C2); }
        public double convert(double d, TimeUnit u) { return u.toMillis(d); }
    },
    SECONDS {
        public double toNanos(double d)   { return x(d, C3/C0, MAX/(C3/C0)); }
        public double toMicros(double d)  { return x(d, C3/C1, MAX/(C3/C1)); }
        public double toMillis(double d)  { return x(d, C3/C2, MAX/(C3/C2)); }
        public double toSeconds(double d) { return d; }
        public double convert(double d, TimeUnit u) { return u.toSeconds(d); }
    },
    SAMPLES {
    	
    },
    EVENTS {
    	
    },
    TURNS {
    	
    };
	
    // Handy constants for conversion methods
    static final double C0 = 1;
    static final double C1 = C0 * 1000;
    static final double C2 = C1 * 1000;
    static final double C3 = C2 * 1000;

    static final double MAX = Double.MAX_VALUE;
	
    /**
     * Scale d by m, checking for overflow.
     * This has a short name to make above code more readable.
     */
    static double x(double d, double m, double over) {
        if (d >  over) return Double.MAX_VALUE;
        if (d < -over) return Double.MIN_VALUE;
        return d * m;
    }

    public double convert(double sourceDuration, TimeUnit sourceUnit) {
        throw new AbstractMethodError();
    }

    public double toNanos(double duration) {
        throw new AbstractMethodError();
    }

    public double toMicros(double duration) {
        throw new AbstractMethodError();
    }

    public double toMillis(double duration) {
        throw new AbstractMethodError();
    }

    public double toSeconds(double duration) {
        throw new AbstractMethodError();
    }

}