/*
 * Ivory: A Hadoop toolkit for web-scale information retrieval
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package ivory.smrf.model;

/**
 * @author Don Metzler
 */
public class Parameter {
  public static final String DEFAULT = "default";
  public static final String TERM_ID = "termWt";
  public static final String QUERY_TERM_ID = "query-termWt";
  public static final String ORDERED_ID = "orderedWt";
  public static final String QUERY_ORDERED_ID = "query-orderedWt";
  public static final String UNORDERED_ID = "unorderedWt";
  public static final String QUERY_UNORDERED_ID = "query-unorderedWt";

  public static final String BIGRAM_ID = "bigramWt";

  private String name;
  private float weight;

  public Parameter(String name, float weight) {
    this.name = name;
    this.weight = weight;
  }

  public String getName() {
    return name;
  }

  public float getWeight() {
    return weight;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  @Override
  public String toString() {
    return "[name:" + name + ", weight: " + weight + "]";
  }
}
