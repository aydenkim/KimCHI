package shapes;

import java.util.ArrayList;
import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;

public class DigitNine extends AbstractShape{

	private final static double DISTANCE = 30;

	public DigitNine(){
		requiredComponents.add("loop");
		requiredComponents.add("line");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "nine";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;
		
		Shape line = null;
		Shape loop = null;
		for(Shape shape : components){
			if(shape.getInterpretation().label.equals("line"))
				line = shape;
			if(shape.getInterpretation().label.equals("loop"))
				loop = shape;
		}
		List<Integer> compared_points = new ArrayList<Integer>();
		double length =  components.get(0).getFirstStroke().getPathLength();
		Point start_point = null; 
		Point end_point = null; 
		Shape new_shape = null;
		double index_threshold = 5; // shape_points.size() * 0.15; 
		double distance_threshold = length / 9;

		List<Point> shape_points = components.get(0).getPoints(); 
		for(int i = 0; i < shape_points.size() ; i++){
			start_point = shape_points.get(i);
			for(int j = i + (int)Math.floor(index_threshold) ; j < shape_points.size(); j++){
				end_point = shape_points.get(j);
				if(!compared_points.contains(i) && !compared_points.contains(j)){
					//System.out.println(start_point.distance(end_point));
					if(start_point.distance(end_point) <= distance_threshold){
						if(compared_points.size() == 0){
							compared_points.add(i);
							compared_points.add(j);
							i = i + (int)Math.floor(index_threshold);
						}else{
							if(compared_points.get(compared_points.size()-1) - j >= index_threshold){
								compared_points.add(i);
								compared_points.add(j);
								i = i + (int)Math.floor(index_threshold);
							}
						}
					}		
				}
			}
		}

        Double small_x = Double.MAX_VALUE;
        for(Point point : loop.getPoints()){
        	if(small_x > point.getX()){
        		small_x = point.getX();
        	}
        }
		

		if(compared_points.size() == 2){
			if( (compared_points.get(0) == 0)  && ( Math.abs(compared_points.get(1) - shape_points.size()-1) <= index_threshold )){				
				if(loop.getPoints().get(loop.getPoints().size()-1).getY() < line.getPoints().get(line.getPoints().size()-1).getY()){
					//System.out.println("small_x " + small_x + " loop.getPoints().get(0).getX() " + loop.getPoints().get(0).getX());
					if(small_x < loop.getPoints().get(0).getX()){
						components.get(0).setLabel("nine");
						System.out.println("It's a digit nine");
					}
				}
				
			}
		}
		/*Point point1 = null;
		Point point2 = null;
		if(components.get(0).getInterpretation().label.equals("up")){
			point1 = components.get(0).getPoints().get(components.get(0).getPoints().size()-1);
		}
		if(components.get(1).getInterpretation().label.equals("loop")){
			point2 = components.get(1).getPoints().get(0);
		}

		// check the distance between a curved line and a loop. They should be near to each other
		if(point1 != null && point2 != null){
			if(point1.distance(point2) <= DISTANCE){
				System.out.println("It's a digit six");
				components.get(0).setLabel("six");
			}else{
				return null;
			}
		}*/

		return components.get(0);
	}

}
