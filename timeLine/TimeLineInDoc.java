package timeLine;

import java.util.ArrayList;

import Antology.Triplet;

public class TimeLineInDoc {
	public void findTimeLineInOneDoc(ArrayList<DocumentTripleElement> DTEList) {
		for(DocumentTripleElement DTE : DTEList) {
			ArrayList<Triplet> allTris = new ArrayList<Triplet>();
			if(DTE.titleTriples!=null)
				allTris.addAll(DTE.titleTriples);
			if(DTE.sentenceTriples!=null)
				allTris.addAll(DTE.sentenceTriples);
			DTE.sortedTriples = new ArrayList<Triplet>();
			for(Triplet tri : allTris) {
				if(tri.realTemporal!=null) {
					if(DTE.sortedTriples.size()==0) {
						DTE.sortedTriples.add(tri);
					} else {
						boolean flag = false;
						for(int i=0; i<DTE.sortedTriples.size(); i++) {
							if(tri.realTemporal.compareTo(DTE.sortedTriples.get(i).realTemporal)<0) {
								DTE.sortedTriples.add(i, tri);
								flag = true;
								break;
							}
						}
						if(flag)
							continue;
						DTE.sortedTriples.add(tri);
					}
				}
			}
		}
	}
}
