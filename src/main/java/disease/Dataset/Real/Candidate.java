package disease.Dataset.Real;

import java.util.Objects;

public class Candidate { 
    public String code; 
    public Double score; 
    public int phase; 

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.code);
        hash = 83 * hash + Objects.hashCode(this.score);
        hash = 83 * hash + this.phase;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Candidate other = (Candidate) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.score, other.score)) {
            return false;
        }
        if (this.phase != other.phase) {
            return false;
        }
        return true;
    }

}